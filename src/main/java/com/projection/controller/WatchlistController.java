package com.projection.controller;

import com.projection.dto.content.AddToWatchlistRequestDto;
import com.projection.dto.content.WatchlistItemResponseDto;
import com.projection.entity.enums.ContentType;
import com.projection.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping("/{userId}")
    public ResponseEntity<WatchlistItemResponseDto> addToWatchlist(
            @PathVariable Long userId,
            @Valid @RequestBody AddToWatchlistRequestDto request) {
        log.info("Received add to watchlist request for user ID: {}", userId);
        WatchlistItemResponseDto response = watchlistService.addToWatchlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeFromWatchlist(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Received remove from watchlist request for user ID: {}", userId);
        watchlistService.removeFromWatchlist(userId, tmdbId, contentType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<WatchlistItemResponseDto>> getUserWatchlist(@PathVariable Long userId) {
        log.info("Received get watchlist request for user ID: {}", userId);
        List<WatchlistItemResponseDto> watchlist = watchlistService.getUserWatchlist(userId);
        return ResponseEntity.ok(watchlist);
    }

    @GetMapping("/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkIsInWatchlist(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Checking if content is in watchlist for user ID: {}", userId);
        boolean isInWatchlist = watchlistService.isInWatchlist(userId, tmdbId, contentType);
        return ResponseEntity.ok(Map.of("isInWatchlist", isInWatchlist));
    }
}
