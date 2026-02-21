package com.projection.repository;

import com.projection.entity.content.ContentReference;
import com.projection.entity.rating.UserRating;
import com.projection.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    Optional<UserRating> findByUserAndContent(User user, ContentReference content);

    @Query("SELECT ur FROM UserRating ur WHERE ur.user.id = :userId AND ur.content.tmdbId = :tmdbId AND CAST(ur.content.contentType AS string) = :contentType")
    Optional<UserRating> findByUserIdAndTmdbIdAndContentType(
            @Param("userId") Long userId,
            @Param("tmdbId") Long tmdbId,
            @Param("contentType") String contentType);

    List<UserRating> findByUserIdOrderByUpdatedAtDesc(Long userId);

    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.content.tmdbId = :tmdbId AND CAST(ur.content.contentType AS string) = :contentType")
    Double getAverageRatingForContent(@Param("tmdbId") Long tmdbId, @Param("contentType") String contentType);

    @Query("SELECT COUNT(ur) FROM UserRating ur WHERE ur.content.tmdbId = :tmdbId AND CAST(ur.content.contentType AS string) = :contentType")
    Long countRatingsForContent(@Param("tmdbId") Long tmdbId, @Param("contentType") String contentType);

    void deleteByUserAndContent(User user, ContentReference content);
}
