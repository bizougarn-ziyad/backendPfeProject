package com.projection.service;

import com.projection.dto.content.AddToWatchlistRequestDto;
import com.projection.dto.content.WatchlistItemResponseDto;
import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.list.ListItem;
import com.projection.entity.list.UserList;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ContentReferenceRepository;
import com.projection.repository.ListItemRepository;
import com.projection.repository.UserListRepository;
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
public class WatchlistService {

    private final UserListRepository userListRepository;
    private final ListItemRepository listItemRepository;
    private final UserRepository userRepository;
    private final ContentReferenceRepository contentReferenceRepository;

    private static final String WATCHLIST_NAME = "Watchlist";

    @Transactional
    public WatchlistItemResponseDto addToWatchlist(Long userId, AddToWatchlistRequestDto request) {
        log.info("Adding content to watchlist for user ID: {}, TMDB ID: {}, Type: {}",
                userId, request.getTmdbId(), request.getContentType());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Get or create Watchlist
        UserList watchlist = userListRepository.findByUserIdAndDefaultName(userId, WATCHLIST_NAME)
                .orElseGet(() -> {
                    log.info("Creating default watchlist for user ID: {}", userId);
                    UserList newWatchlist = UserList.builder()
                            .user(user)
                            .name(WATCHLIST_NAME)
                            .description("My watchlist")
                            .isDefault(true)
                            .isPublic(false)
                            .build();
                    return userListRepository.save(newWatchlist);
                });

        // Check if already in watchlist
        if (listItemRepository.existsByListIdAndContent(watchlist.getId(), request.getTmdbId(),
                request.getContentType())) {
            log.info("Content already in watchlist");
            throw new IllegalStateException("Content is already in watchlist");
        }

        // Get or create ContentReference
        ContentReference contentReference = contentReferenceRepository
                .findByTmdbIdAndContentType(request.getTmdbId(), request.getContentType())
                .orElseGet(() -> {
                    ContentReference newContent = ContentReference.builder()
                            .tmdbId(request.getTmdbId())
                            .contentType(request.getContentType())
                            .build();
                    return contentReferenceRepository.save(newContent);
                });

        // Create list item
        ListItem listItem = ListItem.builder()
                .userList(watchlist)
                .contentReference(contentReference)
                .notes(request.getNotes())
                .build();

        listItem = listItemRepository.save(listItem);
        log.info("Content added to watchlist successfully");

        return convertToDto(listItem);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long tmdbId, ContentType contentType) {
        log.info("Removing content from watchlist for user ID: {}, TMDB ID: {}, Type: {}",
                userId, tmdbId, contentType);

        UserList watchlist = userListRepository.findByUserIdAndDefaultName(userId, WATCHLIST_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));

        ListItem listItem = listItemRepository.findByListIdAndContent(watchlist.getId(), tmdbId, contentType)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in watchlist"));

        listItemRepository.delete(listItem);
        log.info("Content removed from watchlist successfully");
    }

    @Transactional(readOnly = true)
    public List<WatchlistItemResponseDto> getUserWatchlist(Long userId) {
        log.info("Fetching watchlist for user ID: {}", userId);

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        UserList watchlist = userListRepository.findByUserIdAndDefaultName(userId, WATCHLIST_NAME)
                .orElse(null);

        if (watchlist == null) {
            return List.of();
        }

        List<ListItem> items = listItemRepository.findByListId(watchlist.getId());
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isInWatchlist(Long userId, Long tmdbId, ContentType contentType) {
        UserList watchlist = userListRepository.findByUserIdAndDefaultName(userId, WATCHLIST_NAME)
                .orElse(null);

        if (watchlist == null) {
            return false;
        }

        return listItemRepository.existsByListIdAndContent(watchlist.getId(), tmdbId, contentType);
    }

    private WatchlistItemResponseDto convertToDto(ListItem listItem) {
        return WatchlistItemResponseDto.builder()
                .id(listItem.getId().toString())
                .tmdbId(listItem.getContentReference().getTmdbId())
                .contentType(listItem.getContentReference().getContentType())
                .notes(listItem.getNotes())
                .addedAt(listItem.getAddedAt())
                .build();
    }
}
