package com.projection.controller;

import com.projection.dto.content.AddToFavoriteRequestDto;
import com.projection.dto.content.FavoriteResponseDto;
import com.projection.entity.enums.ContentType;
import com.projection.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{userId}")
    public ResponseEntity<FavoriteResponseDto> addToFavorites(
            @PathVariable Long userId,
            @Valid @RequestBody AddToFavoriteRequestDto request) {
        log.info("Received add to favorites request for user ID: {}", userId);
        FavoriteResponseDto response = favoriteService.addToFavorites(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Received remove from favorites request for user ID: {}", userId);
        favoriteService.removeFromFavorites(userId, tmdbId, contentType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponseDto>> getUserFavorites(@PathVariable Long userId) {
        log.info("Received get favorites request for user ID: {}", userId);
        List<FavoriteResponseDto> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkIsFavorite(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Checking if content is favorite for user ID: {}", userId);
        boolean isFavorite = favoriteService.isFavorite(userId, tmdbId, contentType);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
}
