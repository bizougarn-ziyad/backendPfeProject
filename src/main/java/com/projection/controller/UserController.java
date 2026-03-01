package com.projection.controller;

import com.projection.dto.auth.AuthResponseDto;
import com.projection.dto.user.ChangePasswordRequestDto;
import com.projection.dto.user.UpdateProfileRequestDto;
import com.projection.dto.user.UserSearchResultDto;
import com.projection.dto.user.UserStatsDto;
import com.projection.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResultDto>> searchUsers(@RequestParam String query) {
        log.info("Received search request for users with query: {}", query);
        List<UserSearchResultDto> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsDto> getUserStats(@PathVariable Long userId) {
        log.info("Received request for user stats for user ID: {}", userId);
        UserStatsDto stats = userService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<AuthResponseDto> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequestDto updateRequest) {
        log.info("Received update profile request for user ID: {}", userId);
        AuthResponseDto response = userService.updateProfile(userId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequestDto changePasswordRequest) {
        log.info("Received change password request for user ID: {}", userId);
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
