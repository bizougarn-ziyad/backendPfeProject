package com.projection.service;

import com.projection.dto.content.AddToFavoriteRequestDto;
import com.projection.dto.content.FavoriteResponseDto;
import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.user.User;
import com.projection.entity.user.UserFavorite;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ContentReferenceRepository;
import com.projection.repository.UserFavoriteRepository;
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
public class FavoriteService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;
    private final ContentReferenceRepository contentReferenceRepository;

    @Transactional
    public FavoriteResponseDto addToFavorites(Long userId, AddToFavoriteRequestDto request) {
        log.info("Adding content to favorites for user ID: {}, TMDB ID: {}, Type: {}",
                userId, request.getTmdbId(), request.getContentType());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if already in favorites
        if (userFavoriteRepository.existsByUserIdAndContent(userId, request.getTmdbId(), request.getContentType())) {
            log.info("Content already in favorites");
            throw new IllegalStateException("Content is already in favorites");
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

        // Create favorite
        UserFavorite favorite = UserFavorite.builder()
                .user(user)
                .contentReference(contentReference)
                .build();

        favorite = userFavoriteRepository.save(favorite);
        log.info("Content added to favorites successfully");

        return convertToDto(favorite);
    }

    @Transactional
    public void removeFromFavorites(Long userId, Long tmdbId, ContentType contentType) {
        log.info("Removing content from favorites for user ID: {}, TMDB ID: {}, Type: {}",
                userId, tmdbId, contentType);

        UserFavorite favorite = userFavoriteRepository.findByUserIdAndContent(userId, tmdbId, contentType)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        userFavoriteRepository.delete(favorite);
        log.info("Content removed from favorites successfully");
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> getUserFavorites(Long userId) {
        log.info("Fetching favorites for user ID: {}", userId);

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<UserFavorite> favorites = userFavoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long tmdbId, ContentType contentType) {
        return userFavoriteRepository.existsByUserIdAndContent(userId, tmdbId, contentType);
    }

    private FavoriteResponseDto convertToDto(UserFavorite favorite) {
        return FavoriteResponseDto.builder()
                .id(favorite.getId().toString())
                .tmdbId(favorite.getContentReference().getTmdbId())
                .contentType(favorite.getContentReference().getContentType())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
