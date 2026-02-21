package com.projection.service;

import com.projection.dto.auth.AuthResponseDto;
import com.projection.dto.auth.LoginRequestDto;
import com.projection.dto.auth.SignUpRequestDto;
import com.projection.entity.enums.Role;
import com.projection.entity.user.User;
import com.projection.exception.InvalidCredentialsException;
import com.projection.exception.UserAlreadyExistsException;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
