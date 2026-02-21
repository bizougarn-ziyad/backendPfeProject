package com.projection.service;

import com.projection.dto.rating.AddRatingRequestDto;
import com.projection.dto.rating.RatingResponseDto;
import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.rating.UserRating;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ContentReferenceRepository;
import com.projection.repository.UserRatingRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final UserRatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ContentReferenceRepository contentReferenceRepository;

    @Transactional
    public RatingResponseDto addOrUpdateRating(Long userId, AddRatingRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If rating is 0, remove the rating
        if (request.getRating() == 0) {
            return removeRating(userId, request.getTmdbId().longValue(), request.getContentType().name());
        }

        ContentReference contentRef = contentReferenceRepository
                .findByTmdbIdAndContentType(request.getTmdbId().longValue(), request.getContentType())
                .orElseGet(() -> {
                    ContentReference newContent = new ContentReference();
                    newContent.setTmdbId(request.getTmdbId().longValue());
                    newContent.setContentType(request.getContentType());
                    return contentReferenceRepository.save(newContent);
                });

        Optional<UserRating> existingRating = ratingRepository.findByUserAndContent(user, contentRef);

        UserRating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setRating(request.getRating());
        } else {
            rating = new UserRating();
            rating.setUser(user);
            rating.setContent(contentRef);
            rating.setRating(request.getRating());
        }

        rating = ratingRepository.save(rating);
        return mapToDto(rating);
    }

    @Transactional
    public RatingResponseDto removeRating(Long userId, Long tmdbId, String contentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ContentType contentTypeEnum = ContentType.valueOf(contentType);
        Optional<ContentReference> contentRefOpt = contentReferenceRepository
                .findByTmdbIdAndContentType(tmdbId.longValue(), contentTypeEnum);

        if (contentRefOpt.isPresent()) {
            Optional<UserRating> ratingOpt = ratingRepository.findByUserAndContent(user, contentRefOpt.get());
            if (ratingOpt.isPresent()) {
                UserRating rating = ratingOpt.get();
                RatingResponseDto response = mapToDto(rating);
                ratingRepository.delete(rating);
                return response;
            }
        }

        throw new ResourceNotFoundException("Rating not found");
    }

    @Transactional(readOnly = true)
    public Optional<Double> getUserRating(Long userId, Long tmdbId, String contentType) {
        return ratingRepository.findByUserIdAndTmdbIdAndContentType(userId, tmdbId, contentType)
                .map(UserRating::getRating);
    }

    @Transactional(readOnly = true)
    public boolean hasUserRated(Long userId, Long tmdbId, String contentType) {
        return ratingRepository.findByUserIdAndTmdbIdAndContentType(userId, tmdbId, contentType)
                .isPresent();
    }

    @Transactional(readOnly = true)
    public List<RatingResponseDto> getUserRatings(Long userId) {
        return ratingRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long tmdbId, String contentType) {
        Double avg = ratingRepository.getAverageRatingForContent(tmdbId, contentType);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : null;
    }

    @Transactional(readOnly = true)
    public Long getRatingCount(Long tmdbId, String contentType) {
        return ratingRepository.countRatingsForContent(tmdbId, contentType);
    }

    private RatingResponseDto mapToDto(UserRating rating) {
        RatingResponseDto dto = new RatingResponseDto();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUser().getId());
        dto.setTmdbId(rating.getContent().getTmdbId().intValue());
        dto.setContentType(rating.getContent().getContentType());
        dto.setRating(rating.getRating());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUpdatedAt(rating.getUpdatedAt());
        return dto;
    }
}
