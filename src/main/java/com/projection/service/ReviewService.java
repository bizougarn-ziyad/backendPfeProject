package com.projection.service;

import com.projection.dto.review.ReviewResponseDto;
import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.review.Review;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ContentReferenceRepository;
import com.projection.repository.ReviewRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ContentReferenceRepository contentReferenceRepository;

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getUserReviews(Long userId) {
        log.info("Fetching reviews for user ID: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getContentReviews(Long tmdbId, String contentType) {
        log.info("Fetching reviews for tmdbId: {} contentType: {}", tmdbId, contentType);
        ContentType ct = ContentType.valueOf(contentType);
        List<Review> reviews = reviewRepository.findByTmdbIdAndContentType(tmdbId, ct);
        return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDto createOrUpdateReview(Long userId, Long tmdbId, String contentType, Integer rating, String reviewText) {
        log.info("Creating/updating review for user {} on tmdbId {} ({})", userId, tmdbId, contentType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ContentType ct = ContentType.valueOf(contentType);
        ContentReference contentRef = contentReferenceRepository
                .findByTmdbIdAndContentType(tmdbId, ct)
                .orElseGet(() -> {
                    ContentReference newRef = new ContentReference();
                    newRef.setTmdbId(tmdbId);
                    newRef.setContentType(ct);
                    return contentReferenceRepository.save(newRef);
                });

        Optional<Review> existing = reviewRepository.findByUserAndContentReference(user, contentRef);

        Review review;
        if (existing.isPresent()) {
            review = existing.get();
            review.setRating(rating);
            review.setReviewText(reviewText);
        } else {
            review = Review.builder()
                    .user(user)
                    .contentReference(contentRef)
                    .rating(rating)
                    .reviewText(reviewText)
                    .build();
        }

        review = reviewRepository.save(review);
        return convertToDto(review);
    }

    @Transactional
    public void deleteReview(String reviewId, Long userId) {
        UUID uuid = UUID.fromString(reviewId);
        Review review = reviewRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
        log.info("Deleted review {} by user {}", reviewId, userId);
    }

    @Transactional
    public void adminDeleteReview(String reviewId) {
        UUID uuid = UUID.fromString(reviewId);
        Review review = reviewRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        reviewRepository.delete(review);
        log.info("Admin deleted review {}", reviewId);
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
