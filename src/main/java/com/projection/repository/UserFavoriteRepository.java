package com.projection.repository;

import com.projection.entity.user.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UUID> {

    @Query("SELECT uf FROM UserFavorite uf WHERE uf.user.id = :userId")
    List<UserFavorite> findByUserId(@Param("userId") Long userId);

    @Query("SELECT uf FROM UserFavorite uf WHERE uf.user.id = :userId AND uf.contentReference.tmdbId = :tmdbId AND uf.contentReference.contentType = :contentType")
    Optional<UserFavorite> findByUserIdAndContent(@Param("userId") Long userId, @Param("tmdbId") Long tmdbId,
            @Param("contentType") com.projection.entity.enums.ContentType contentType);

    @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM UserFavorite uf WHERE uf.user.id = :userId AND uf.contentReference.tmdbId = :tmdbId AND uf.contentReference.contentType = :contentType")
    boolean existsByUserIdAndContent(@Param("userId") Long userId, @Param("tmdbId") Long tmdbId,
            @Param("contentType") com.projection.entity.enums.ContentType contentType);

    @Query("SELECT COUNT(uf) FROM UserFavorite uf WHERE uf.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
