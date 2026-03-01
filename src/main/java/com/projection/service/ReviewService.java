package com.projection.service;

import com.projection.dto.review.ReviewResponseDto;
import com.projection.entity.review.Review;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ReviewRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getUserReviews(Long userId) {
        log.info("Fetching reviews for user ID: {}", userId);

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDto convertToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId().toString())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .tmdbId(review.getContentReference().getTmdbId())
                .contentType(review.getContentReference().getContentType())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .likesCount(review.getLikesCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
