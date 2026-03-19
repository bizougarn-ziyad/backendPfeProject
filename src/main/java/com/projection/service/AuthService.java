package com.projection.service;

import com.projection.dto.auth.AuthResponseDto;
import com.projection.dto.auth.GoogleAuthRequestDto;
import com.projection.dto.auth.LoginRequestDto;
import com.projection.dto.auth.SignUpRequestDto;
import com.projection.entity.enums.Role;
import com.projection.entity.user.User;
import com.projection.exception.InvalidCredentialsException;
import com.projection.exception.UserAlreadyExistsException;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.oauth.client-id:}")
    private String googleClientId;

    @Transactional
    public AuthResponseDto signUp(SignUpRequestDto signUpRequest) {
        log.info("Attempting to create new user with email: {}", signUpRequest.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            log.error("Email already exists: {}", signUpRequest.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            log.error("Username already exists: {}", signUpRequest.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }

        // Create new user
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .isSuspended(false)
                .build();

        user = userRepository.save(user);
        log.info("User created successfully with ID: {}", user.getId());

        return convertToAuthResponse(user, "User registered successfully");
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", loginRequest.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.error("User account is deactivated: {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.error("Invalid password for email: {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getEmail());
        return convertToAuthResponse(user, "Login successful");
    }

    @Transactional
    public AuthResponseDto googleAuth(GoogleAuthRequestDto googleAuthRequest) {
        String idToken = googleAuthRequest.getIdToken();
        Map<String, Object> tokenInfo;
        try {
            tokenInfo = verifyGoogleIdToken(idToken);
        } catch (InvalidCredentialsException e) {
            log.warn("Google auth rejected: {}", e.getMessage());
            throw e;
        }

        String email = stringValue(tokenInfo.get("email"));
        String emailVerified = stringValue(tokenInfo.get("email_verified"));
        String audience = stringValue(tokenInfo.get("aud"));
        String fullName = stringValue(tokenInfo.get("name"));
        String pictureUrl = stringValue(tokenInfo.get("picture"));

        if (email == null || email.isBlank()) {
            throw new InvalidCredentialsException("Google account email is missing");
        }

        if (!"true".equalsIgnoreCase(emailVerified)) {
            throw new InvalidCredentialsException("Google account email is not verified");
        }

        if (googleClientId != null && !googleClientId.isBlank() && !googleClientId.equals(audience)) {
            throw new InvalidCredentialsException("Google token audience does not match this application");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        boolean isNewUser = false;

        if (user == null) {
            isNewUser = true;
            String username = buildUniqueUsername(googleAuthRequest.getPreferredUsername(), email);
            user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .isActive(true)
                    .isSuspended(false)
                    .bio((fullName == null || fullName.isBlank()) ? null : "Hi, I'm " + fullName + "!")
                    .profilePictureUrl((pictureUrl == null || pictureUrl.isBlank()) ? null : pictureUrl)
                    .build();
        } else if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        if ((user.getProfilePictureUrl() == null || user.getProfilePictureUrl().isBlank())
                && pictureUrl != null && !pictureUrl.isBlank()) {
            user.setProfilePictureUrl(pictureUrl);
        }

        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);

        return convertToAuthResponse(user, isNewUser ? "Google signup successful" : "Google login successful");
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Username check called with null or empty username");
            return false;
        }
        boolean exists = userRepository.existsByUsername(username);
        boolean isAvailable = !exists;
        log.info("Username '{}' - exists: {}, available: {}", username, exists, isAvailable);
        return isAvailable;
    }

    private Map<String, Object> verifyGoogleIdToken(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new InvalidCredentialsException("Google ID token is required");
        }

        String url = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/tokeninfo")
                .queryParam("id_token", idToken)
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null || body.isEmpty()) {
                throw new InvalidCredentialsException("Invalid Google token");
            }
            if (body.containsKey("error_description") || body.containsKey("error")) {
                throw new InvalidCredentialsException("Invalid Google token");
            }
            return body;
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify Google token", e);
            throw new InvalidCredentialsException("Failed to verify Google token");
        }
    }

    private String buildUniqueUsername(String preferredUsername, String email) {
        String base = sanitizeUsername(preferredUsername);

        if (base == null || base.isBlank()) {
            String emailPrefix = email.split("@")[0];
            base = sanitizeUsername(emailPrefix);
        }

        if (base == null || base.isBlank()) {
            base = "user";
        }

        if (base.length() < 3) {
            base = (base + "user").substring(0, 4);
        }

        if (base.length() > 50) {
            base = base.substring(0, 50);
        }

        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            String suffixText = String.valueOf(suffix++);
            int maxBaseLength = 50 - suffixText.length() - 1;
            String trimmedBase = base.length() > maxBaseLength ? base.substring(0, maxBaseLength) : base;
            candidate = trimmedBase + "_" + suffixText;
        }

        return candidate;
    }

    private String sanitizeUsername(String raw) {
        if (raw == null)
            return null;
        String cleaned = raw.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+|_+$", "");
        return cleaned;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private AuthResponseDto convertToAuthResponse(User user, String message) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .message(message)
                .build();
    }
}
