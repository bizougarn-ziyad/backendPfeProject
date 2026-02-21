package com.projection.repository;

import com.projection.entity.user.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {

    @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.follower.id = :userId")
    long countByFollowerId(@Param("userId") Long userId);

    @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.following.id = :userId")
    long countByFollowingId(@Param("userId") Long userId);
}
