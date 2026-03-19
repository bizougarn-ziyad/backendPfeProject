package com.projection.controller;

import com.projection.dto.auth.AuthResponseDto;
import com.projection.dto.auth.GoogleAuthRequestDto;
import com.projection.dto.auth.LoginRequestDto;
import com.projection.dto.auth.SignUpRequestDto;
import com.projection.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        log.info("Received signup request for email: {}", signUpRequest.getEmail());
        AuthResponseDto response = authService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Received login request for email: {}", loginRequest.getEmail());
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleAuth(@Valid @RequestBody GoogleAuthRequestDto googleAuthRequest) {
        log.info("Received Google auth request");
        AuthResponseDto response = authService.googleAuth(googleAuthRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        log.info("Checking username availability: {}", username);
        boolean isAvailable = authService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }
}
