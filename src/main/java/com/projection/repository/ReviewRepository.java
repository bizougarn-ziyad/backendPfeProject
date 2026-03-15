package com.projection.repository;

import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.review.Review;
import com.projection.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.contentReference.contentType = :contentType")
    long countByUserIdAndContentType(@Param("userId") Long userId, @Param("contentType") ContentType contentType);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT r FROM Review r WHERE r.contentReference.tmdbId = :tmdbId AND r.contentReference.contentType = :contentType ORDER BY r.createdAt DESC")
    List<Review> findByTmdbIdAndContentType(@Param("tmdbId") Long tmdbId, @Param("contentType") ContentType contentType);

    Optional<Review> findByUserAndContentReference(User user, ContentReference contentReference);
}
