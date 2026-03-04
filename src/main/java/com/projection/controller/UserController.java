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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSearchResultDto>> getFollowers(@PathVariable Long userId) {
        log.info("Received request for followers of user ID: {}", userId);
        List<UserSearchResultDto> followers = userService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSearchResultDto>> getFollowing(@PathVariable Long userId) {
        log.info("Received request for following of user ID: {}", userId);
        List<UserSearchResultDto> following = userService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @PostMapping("/{followerId}/follow/{followingId}")
    public ResponseEntity<Void> followUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        log.info("User {} attempting to follow user {}", followerId, followingId);
        userService.followUser(followerId, followingId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        log.info("User {} attempting to unfollow user {}", followerId, followingId);
        userService.unfollowUser(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{followerId}/is-following/{followingId}")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        log.info("Checking if user {} is following user {}", followerId, followingId);
        boolean isFollowing = userService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    @GetMapping("/{followerId}/follow-status/{followingId}")
    public ResponseEntity<Map<String, String>> getFollowStatus(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        log.info("Getting follow status from user {} to user {}", followerId, followingId);
        String status = userService.getFollowStatus(followerId, followingId);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/{userId}/follow-requests")
    public ResponseEntity<java.util.List<com.projection.dto.user.UserSearchResultDto>> getPendingFollowRequests(
            @PathVariable Long userId) {
        log.info("Fetching pending follow requests for user ID: {}", userId);
        var requests = userService.getPendingFollowRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{userId}/accept-follow/{requesterId}")
    public ResponseEntity<Void> acceptFollowRequest(
            @PathVariable Long userId,
            @PathVariable Long requesterId) {
        log.info("User {} accepting follow request from user {}", userId, requesterId);
        userService.acceptFollowRequest(requesterId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/decline-follow/{requesterId}")
    public ResponseEntity<Void> declineFollowRequest(
            @PathVariable Long userId,
            @PathVariable Long requesterId) {
        log.info("User {} declining follow request from user {}", userId, requesterId);
        userService.declineFollowRequest(requesterId, userId);
        return ResponseEntity.noContent().build();
    }
}
