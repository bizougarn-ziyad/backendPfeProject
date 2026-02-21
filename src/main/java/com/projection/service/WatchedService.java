package com.projection.service;

import com.projection.dto.content.AddToWatchedRequestDto;
import com.projection.dto.content.WatchedResponseDto;
import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.user.User;
import com.projection.entity.user.UserWatched;
import com.projection.repository.ContentReferenceRepository;
import com.projection.repository.UserRepository;
import com.projection.repository.UserWatchedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchedService {

    private final UserWatchedRepository userWatchedRepository;
    private final UserRepository userRepository;
    private final ContentReferenceRepository contentReferenceRepository;

    @Transactional
    public WatchedResponseDto markAsWatched(Long userId, AddToWatchedRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Find or create content reference
        ContentReference contentRef = contentReferenceRepository
                .findByTmdbIdAndContentType(request.getTmdbId(), request.getContentType())
                .orElseGet(() -> {
                    ContentReference newContentRef = ContentReference.builder()
                            .tmdbId(request.getTmdbId())
                            .contentType(request.getContentType())
                            .build();
                    return contentReferenceRepository.save(newContentRef);
                });

        // Check if already watched
        boolean alreadyWatched = userWatchedRepository
                .existsByUserIdAndContentReferenceId(userId, contentRef.getId());

        if (alreadyWatched) {
            log.info("Content already marked as watched for user: {}", userId);
            UserWatched existing = userWatchedRepository
                    .findByUserIdAndContentReferenceId(userId, contentRef.getId())
                    .orElseThrow();
            return mapToDto(existing);
        }

        // Create new watched entry
        UserWatched userWatched = UserWatched.builder()
                .user(user)
                .contentReference(contentRef)
                .build();

        userWatched = userWatchedRepository.save(userWatched);
        log.info("Content marked as watched for user: {}", userId);

        return mapToDto(userWatched);
    }

    @Transactional
    public void unmarkAsWatched(Long userId, Long tmdbId, ContentType contentType) {
        ContentReference contentRef = contentReferenceRepository
                .findByTmdbIdAndContentType(tmdbId, contentType)
                .orElseThrow(() -> new RuntimeException("Content reference not found"));

        UserWatched userWatched = userWatchedRepository
                .findByUserIdAndContentReferenceId(userId, contentRef.getId())
                .orElseThrow(() -> new RuntimeException("Watched entry not found"));

        userWatchedRepository.delete(userWatched);
        log.info("Content unmarked as watched for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public List<WatchedResponseDto> getUserWatchedContent(Long userId) {
        List<UserWatched> watchedList = userWatchedRepository.findByUserId(userId);
        return watchedList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isWatched(Long userId, Long tmdbId, ContentType contentType) {
        return contentReferenceRepository
                .findByTmdbIdAndContentType(tmdbId, contentType)
                .map(contentRef -> userWatchedRepository
                        .existsByUserIdAndContentReferenceId(userId, contentRef.getId()))
                .orElse(false);
    }

    private WatchedResponseDto mapToDto(UserWatched userWatched) {
        return WatchedResponseDto.builder()
                .tmdbId(userWatched.getContentReference().getTmdbId())
                .contentType(userWatched.getContentReference().getContentType())
                .watchedAt(userWatched.getWatchedAt())
                .build();
    }
}
