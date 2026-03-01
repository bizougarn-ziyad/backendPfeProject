package com.projection.controller;

import com.projection.dto.review.ReviewResponseDto;
import com.projection.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@PathVariable Long userId) {
        log.info("Received get reviews request for user ID: {}", userId);
        List<ReviewResponseDto> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }
}
