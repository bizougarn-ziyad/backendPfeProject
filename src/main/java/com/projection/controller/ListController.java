package com.projection.controller;

import com.projection.dto.content.AddToListRequestDto;
import com.projection.dto.content.CreateListRequestDto;
import com.projection.dto.content.ListItemResponseDto;
import com.projection.dto.content.ListResponseDto;
import com.projection.entity.enums.ContentType;
import com.projection.service.ListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class ListController {

    private final ListService listService;

    /**
     * Get all custom lists for a user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ListResponseDto>> getUserLists(@PathVariable Long userId) {
        log.info("Fetching lists for user ID: {}", userId);
        List<ListResponseDto> lists = listService.getUserLists(userId);
        return ResponseEntity.ok(lists);
    }

    /**
     * Create a new custom list
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ListResponseDto> createList(
            @PathVariable Long userId,
            @Valid @RequestBody CreateListRequestDto request) {
        log.info("Creating list for user ID: {}", userId);
        ListResponseDto response = listService.createList(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete a list
     */
    @DeleteMapping("/{userId}/{listId}")
    public ResponseEntity<Void> deleteList(
            @PathVariable Long userId,
            @PathVariable UUID listId) {
        log.info("Deleting list ID: {} for user ID: {}", listId, userId);
        listService.deleteList(listId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get items in a list
     */
    @GetMapping("/{userId}/{listId}/items")
    public ResponseEntity<ListResponseDto> getListItems(
            @PathVariable Long userId,
            @PathVariable UUID listId) {
        log.info("Fetching items for list ID: {}", listId);
        ListResponseDto response = listService.getListWithItems(listId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Add content to a list
     */
    @PostMapping("/{userId}/{listId}/items")
    public ResponseEntity<ListItemResponseDto> addToList(
            @PathVariable Long userId,
            @PathVariable UUID listId,
            @Valid @RequestBody AddToListRequestDto request) {
        log.info("Adding item to list ID: {} for user ID: {}", listId, userId);
        ListItemResponseDto response = listService.addToList(listId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Remove content from a list
     */
    @DeleteMapping("/{userId}/{listId}/items")
    public ResponseEntity<Void> removeFromList(
            @PathVariable Long userId,
            @PathVariable UUID listId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Removing item from list ID: {} for user ID: {}", listId, userId);
        listService.removeFromList(listId, userId, tmdbId, contentType);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check which of the user's lists contain a specific content item
     */
    @GetMapping("/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkContentInLists(
            @PathVariable Long userId,
            @RequestParam Long tmdbId,
            @RequestParam ContentType contentType) {
        log.info("Checking lists for user ID: {} containing TMDB ID: {}", userId, tmdbId);
        Map<String, Boolean> result = listService.checkContentInLists(userId, tmdbId, contentType);
        return ResponseEntity.ok(result);
    }
}
