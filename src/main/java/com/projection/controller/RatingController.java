package com.projection.controller;

import com.projection.dto.rating.AddRatingRequestDto;
import com.projection.dto.rating.RatingResponseDto;
import com.projection.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{userId}")
    public ResponseEntity<RatingResponseDto> addOrUpdateRating(
            @PathVariable Long userId,
            @Valid @RequestBody AddRatingRequestDto request) {
        RatingResponseDto response = ratingService.addOrUpdateRating(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}/{tmdbId}/{contentType}")
    public ResponseEntity<Map<String, String>> removeRating(
            @PathVariable Long userId,
            @PathVariable Long tmdbId,
            @PathVariable String contentType) {
        ratingService.removeRating(userId, tmdbId, contentType);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Rating removed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{userId}/{tmdbId}/{contentType}")
    public ResponseEntity<Map<String, Object>> checkUserRating(
            @PathVariable Long userId,
            @PathVariable Long tmdbId,
            @PathVariable String contentType) {
        Map<String, Object> response = new HashMap<>();
        response.put("hasRated", ratingService.hasUserRated(userId, tmdbId, contentType));
        ratingService.getUserRating(userId, tmdbId, contentType)
                .ifPresent(rating -> response.put("rating", rating));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponseDto>> getUserRatings(@PathVariable Long userId) {
        List<RatingResponseDto> ratings = ratingService.getUserRatings(userId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/average/{tmdbId}/{contentType}")
    public ResponseEntity<Map<String, Object>> getContentRatingStats(
            @PathVariable Long tmdbId,
            @PathVariable String contentType) {
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", ratingService.getAverageRating(tmdbId, contentType));
        response.put("ratingCount", ratingService.getRatingCount(tmdbId, contentType));
        return ResponseEntity.ok(response);
    }
}
