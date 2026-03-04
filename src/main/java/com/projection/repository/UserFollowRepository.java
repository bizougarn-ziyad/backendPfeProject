package com.projection.repository;

import com.projection.entity.user.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {

        // Count ALL outgoing follows (PENDING + ACCEPTED) = how many you're following
        @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.follower.id = :userId")
        long countByFollowerId(@Param("userId") Long userId);

        // Count only ACCEPTED incoming follows = your actual follower count
        @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.following.id = :userId AND (uf.status = 'ACCEPTED' OR uf.status IS NULL)")
        long countByFollowingId(@Param("userId") Long userId);

        // Check any status (used for duplicate prevention)
        @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM UserFollow uf " +
                        "WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
        boolean existsByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                        @Param("followingId") Long followingId);

        // Mutual followers check requires ACCEPTED on both sides (NULL = legacy
        // accepted)
        @Query("SELECT CASE WHEN COUNT(uf1) > 0 AND COUNT(uf2) > 0 THEN true ELSE false END " +
                        "FROM UserFollow uf1, UserFollow uf2 " +
                        "WHERE uf1.follower.id = :userId1 AND uf1.following.id = :userId2 AND (uf1.status = 'ACCEPTED' OR uf1.status IS NULL) "
                        +
                        "AND uf2.follower.id = :userId2 AND uf2.following.id = :userId1 AND (uf2.status = 'ACCEPTED' OR uf2.status IS NULL)")
        boolean areMutualFollowers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

        // Only accepted follows for following/follower lists (NULL = legacy accepted)
        @Query("SELECT uf.following FROM UserFollow uf WHERE uf.follower.id = :userId AND (uf.status = 'ACCEPTED' OR uf.status IS NULL) ORDER BY uf.createdAt DESC")
        List<com.projection.entity.user.User> findFollowingByUserId(@Param("userId") Long userId);

        @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.following.id = :userId AND (uf.status = 'ACCEPTED' OR uf.status IS NULL) ORDER BY uf.createdAt DESC")
        List<com.projection.entity.user.User> findFollowersByUserId(@Param("userId") Long userId);

        @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
        Optional<UserFollow> findByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                        @Param("followingId") Long followingId);

        // Returns users who have sent PENDING follow requests to the given user (NULL
        // rows are legacy accepted, not pending)
        @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.following.id = :userId AND uf.status = 'PENDING' ORDER BY uf.createdAt DESC")
        List<com.projection.entity.user.User> findPendingRequestersForUser(@Param("userId") Long userId);

        // Returns the raw status string (NULL means legacy accepted row)
        @Query("SELECT uf.status FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
        Optional<String> getFollowStatus(@Param("followerId") Long followerId,
                        @Param("followingId") Long followingId);
}
