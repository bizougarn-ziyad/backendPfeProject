package com.projection.service;

import com.projection.dto.auth.AuthResponseDto;
import com.projection.dto.user.ChangePasswordRequestDto;
import com.projection.dto.user.UpdateProfileRequestDto;
import com.projection.dto.user.UserSearchResultDto;
import com.projection.dto.user.UserStatsDto;
import com.projection.entity.enums.ContentType;
import com.projection.entity.user.User;
import com.projection.entity.user.UserFollow;
import com.projection.exception.InvalidCredentialsException;
import com.projection.exception.ResourceNotFoundException;
import com.projection.exception.UserAlreadyExistsException;
import com.projection.repository.ReviewRepository;
import com.projection.repository.UserFollowRepository;
import com.projection.repository.UserListRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFollowRepository userFollowRepository;
    private final ReviewRepository reviewRepository;
    private final UserListRepository userListRepository;
    private final com.projection.repository.UserWatchedRepository userWatchedRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    @Transactional
    public AuthResponseDto updateProfile(Long userId, UpdateProfileRequestDto updateRequest) {
        log.info("Updating profile for user ID: {}", userId);

        User user = getUserById(userId);

        // Check if username is being changed and if new username already exists
        if (!user.getUsername().equals(updateRequest.getUsername())) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                log.error("Username already exists: {}", updateRequest.getUsername());
                throw new UserAlreadyExistsException("Username already exists");
            }
            user.setUsername(updateRequest.getUsername());
        }

        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(updateRequest.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                log.error("Email already exists: {}", updateRequest.getEmail());
                throw new UserAlreadyExistsException("Email already exists");
            }
            user.setEmail(updateRequest.getEmail());
        }

        // Update other fields
        user.setBio(updateRequest.getBio());
        user.setCountry(normalizeCountry(updateRequest.getCountry()));
        if (updateRequest.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
        }

        user = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return convertToAuthResponse(user, "Profile updated successfully");
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequest) {
        log.info("Changing password for user ID: {}", userId);

        User user = getUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            log.error("Invalid current password for user ID: {}", userId);
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
    }

    @Transactional(readOnly = true)
    public UserStatsDto getUserStats(Long userId) {
        log.info("Fetching stats for user ID: {}", userId);

        // Verify user exists and get user info
        User user = getUserById(userId);

        long moviesWatched = userWatchedRepository.countByUserIdAndContentType(userId, ContentType.MOVIE);
        long seriesWatched = userWatchedRepository.countByUserIdAndContentType(userId, ContentType.TV);
        long following = userFollowRepository.countByFollowerId(userId);
        long followers = userFollowRepository.countByFollowingId(userId);
        long reviewsCount = reviewRepository.countByUserId(userId);
        long listsCount = userListRepository.countByUserId(userId);

        return UserStatsDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .moviesWatched(moviesWatched)
                .seriesWatched(seriesWatched)
                .following(following)
                .followers(followers)
                .reviewsCount(reviewsCount)
                .listsCount(listsCount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserSearchResultDto> searchUsers(String query) {
        log.info("Searching for users with query: {}", query);

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.searchUsers(query.trim());

        return users.stream()
                .limit(5) // Limit to 5 results
                .map(user -> UserSearchResultDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .bio(user.getBio())
                        .profilePictureUrl(user.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSearchResultDto> getFollowers(Long userId) {
        log.info("Fetching followers for user ID: {}", userId);
        // Verify user exists
        getUserById(userId);

        List<User> followers = userFollowRepository.findFollowersByUserId(userId);
        return followers.stream()
                .map(user -> UserSearchResultDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .bio(user.getBio())
                        .profilePictureUrl(user.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSearchResultDto> getFollowing(Long userId) {
        log.info("Fetching following for user ID: {}", userId);
        // Verify user exists
        getUserById(userId);

        List<User> following = userFollowRepository.findFollowingByUserId(userId);
        return following.stream()
                .map(user -> UserSearchResultDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .bio(user.getBio())
                        .profilePictureUrl(user.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void followUser(Long followerId, Long followingId) {
        log.info("User {} attempting to follow user {}", followerId, followingId);

        // Verify both users exist
        User follower = getUserById(followerId);
        User following = getUserById(followingId);

        // Check if user is trying to follow themselves
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        // Check if already following
        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("Already following this user");
        }

        // Create new follow relationship (direct follow, no approval needed)
        UserFollow userFollow = UserFollow.builder()
                .follower(follower)
                .following(following)
                .status("ACCEPTED")
                .build();

        userFollowRepository.save(userFollow);

        // Notify the followed user in real-time via WebSocket
        try {
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("type", "FOLLOW");
            payload.put("id", follower.getId());
            payload.put("username", follower.getUsername());
            payload.put("profilePictureUrl",
                    follower.getProfilePictureUrl() != null ? follower.getProfilePictureUrl() : "");
            messagingTemplate.convertAndSend("/topic/followers/" + followingId, (Object) payload);
        } catch (Exception e) {
            log.warn("Failed to send WebSocket follow notification: {}", e.getMessage());
        }

        log.info("User {} successfully followed user {}", followerId, followingId);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        log.info("User {} attempting to unfollow user {}", followerId, followingId);

        // Find the follow relationship
        UserFollow userFollow = userFollowRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Follow relationship not found between user " + followerId + " and user " + followingId));

        // Delete the follow relationship
        userFollowRepository.delete(userFollow);

        // Notify the previously-followed user so they can remove the unseen
        // notification
        try {
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("type", "UNFOLLOW");
            payload.put("id", followerId);
            messagingTemplate.convertAndSend("/topic/followers/" + followingId, (Object) payload);
        } catch (Exception e) {
            log.warn("Failed to send WebSocket unfollow notification: {}", e.getMessage());
        }

        log.info("User {} successfully unfollowed user {}", followerId, followingId);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Transactional(readOnly = true)
    public String getFollowStatus(Long followerId, Long followingId) {
        // Use existsBy as ground truth — avoids Optional<String> collapsing NULL to
        // empty
        if (!userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return "NONE";
        }
        Optional<String> status = userFollowRepository.getFollowStatus(followerId, followingId);
        // If row exists but Optional is empty, it means status column is NULL → legacy
        // accepted row
        if (status.isEmpty()) {
            return "ACCEPTED";
        }
        String s = status.get();
        // Defensive null check for any remaining NULL values
        return (s == null || s.isBlank()) ? "ACCEPTED" : s;
    }

    @Transactional(readOnly = true)
    public List<UserSearchResultDto> getPendingFollowRequests(Long userId) {
        log.info("Fetching pending follow requests for user ID: {}", userId);
        getUserById(userId);
        List<com.projection.entity.user.User> requesters = userFollowRepository.findPendingRequestersForUser(userId);
        return requesters.stream()
                .map(user -> UserSearchResultDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .bio(user.getBio())
                        .profilePictureUrl(user.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFollowRequest(Long requesterId, Long userId) {
        log.info("User {} accepting follow request from user {}", userId, requesterId);
        UserFollow follow = userFollowRepository
                .findByFollowerIdAndFollowingId(requesterId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Follow request not found from user " + requesterId + " to user " + userId));
        follow.setStatus("ACCEPTED");
        userFollowRepository.save(follow);
        log.info("Follow request from {} accepted by {}", requesterId, userId);
    }

    @Transactional
    public void declineFollowRequest(Long requesterId, Long userId) {
        log.info("User {} declining follow request from user {}", userId, requesterId);
        UserFollow follow = userFollowRepository
                .findByFollowerIdAndFollowingId(requesterId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Follow request not found from user " + requesterId + " to user " + userId));
        userFollowRepository.delete(follow);
        log.info("Follow request from {} declined by {}", requesterId, userId);
    }

    private AuthResponseDto convertToAuthResponse(User user, String message) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .country(user.getCountry())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .message(message)
                .build();
    }

    private String normalizeCountry(String country) {
        if (country == null) {
            return null;
        }
        String normalized = country.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
