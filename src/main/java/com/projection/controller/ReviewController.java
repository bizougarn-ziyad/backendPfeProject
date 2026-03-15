package com.projection.controller;

import com.projection.dto.review.ReviewResponseDto;
import com.projection.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class ReviewController {

    private final ReviewService reviewService;

    /** Get all reviews for a specific user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@PathVariable Long userId) {
        log.info("Received get reviews request for user ID: {}", userId);
        List<ReviewResponseDto> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    /** Get all reviews for a specific piece of content (tmdbId + contentType) */
    @GetMapping("/content/{tmdbId}")
    public ResponseEntity<List<ReviewResponseDto>> getContentReviews(
            @PathVariable Long tmdbId,
            @RequestParam String contentType) {
        log.info("Received get reviews request for tmdbId: {} contentType: {}", tmdbId, contentType);
        List<ReviewResponseDto> reviews = reviewService.getContentReviews(tmdbId, contentType);
        return ResponseEntity.ok(reviews);
    }

    /** Create or update a review */
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long tmdbId = Long.valueOf(body.get("tmdbId").toString());
        String contentType = body.get("contentType").toString();
        Integer rating = Integer.valueOf(body.get("rating").toString());
        String reviewText = body.get("reviewText") != null ? body.get("reviewText").toString() : "";

        log.info("Creating review: user={}, tmdb={}, type={}, rating={}", userId, tmdbId, contentType, rating);
        ReviewResponseDto review = reviewService.createOrUpdateReview(userId, tmdbId, contentType, rating, reviewText);
        return ResponseEntity.ok(review);
    }

    /** Delete a review */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String reviewId,
            @RequestParam Long userId) {
        log.info("Deleting review {} for user {}", reviewId, userId);
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    /** Admin: force-delete any review */
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<Void> adminDeleteReview(@PathVariable String reviewId) {
        log.info("Admin force-deleting review {}", reviewId);
        reviewService.adminDeleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
