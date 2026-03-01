package com.projection.repository;

import com.projection.entity.user.UserWatched;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWatchedRepository extends JpaRepository<UserWatched, UUID> {

        @Query("SELECT uw FROM UserWatched uw WHERE uw.user.id = :userId")
        List<UserWatched> findByUserId(@Param("userId") Long userId);

        @Query("SELECT uw FROM UserWatched uw WHERE uw.user.id = :userId AND uw.contentReference.id = :contentReferenceId")
        Optional<UserWatched> findByUserIdAndContentReferenceId(
                        @Param("userId") Long userId,
                        @Param("contentReferenceId") UUID contentReferenceId);

        @Query("SELECT CASE WHEN COUNT(uw) > 0 THEN true ELSE false END FROM UserWatched uw WHERE uw.user.id = :userId AND uw.contentReference.id = :contentReferenceId")
        boolean existsByUserIdAndContentReferenceId(
                        @Param("userId") Long userId,
                        @Param("contentReferenceId") UUID contentReferenceId);

        @Query("SELECT COUNT(uw) FROM UserWatched uw WHERE uw.user.id = :userId")
        long countByUserId(@Param("userId") Long userId);

        @Query("SELECT COUNT(uw) FROM UserWatched uw WHERE uw.user.id = :userId AND uw.contentReference.contentType = :contentType")
        long countByUserIdAndContentType(@Param("userId") Long userId,
                        @Param("contentType") com.projection.entity.enums.ContentType contentType);
}
