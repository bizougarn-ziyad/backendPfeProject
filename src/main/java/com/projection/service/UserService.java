package com.projection.service;

import com.projection.dto.auth.AuthResponseDto;
import com.projection.dto.user.ChangePasswordRequestDto;
import com.projection.dto.user.UpdateProfileRequestDto;
import com.projection.dto.user.UserSearchResultDto;
import com.projection.dto.user.UserStatsDto;
import com.projection.entity.enums.ContentType;
import com.projection.entity.user.User;
import com.projection.exception.InvalidCredentialsException;
import com.projection.exception.ResourceNotFoundException;
import com.projection.exception.UserAlreadyExistsException;
import com.projection.repository.ReviewRepository;
import com.projection.repository.UserFollowRepository;
import com.projection.repository.UserListRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
