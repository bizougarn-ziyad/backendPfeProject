package com.projection.controller;

import com.projection.dto.content.AddToWatchedRequestDto;
import com.projection.dto.content.WatchedResponseDto;
import com.projection.entity.enums.ContentType;
import com.projection.service.WatchedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watched")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class WatchedController {

    private final WatchedService watchedService;

    @PostMapping("/{userId}")
    public ResponseEntity<WatchedResponseDto> markAsWatched(
            @PathVariable Long userId,
            @Valid @RequestBody AddToWatchedRequestDto request) {
        log.info("Received mark as watched request for user ID: {}", userId);
        WatchedResponseDto response = watchedService.markAsWatched(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unmarkAsWatched(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Received unmark as watched request for user ID: {}", userId);
        watchedService.unmarkAsWatched(userId, tmdbId, contentType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<WatchedResponseDto>> getUserWatchedContent(@PathVariable Long userId) {
        log.info("Received get watched content request for user ID: {}", userId);
        List<WatchedResponseDto> watchedContent = watchedService.getUserWatchedContent(userId);
        return ResponseEntity.ok(watchedContent);
    }

    @GetMapping("/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkIfWatched(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Checking if content is watched for user ID: {}", userId);
        boolean isWatched = watchedService.isWatched(userId, tmdbId, contentType);
        return ResponseEntity.ok(Map.of("isWatched", isWatched));
    }
}
