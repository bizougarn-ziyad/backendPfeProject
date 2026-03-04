package com.projection.repository;

import com.projection.entity.community.CommunityUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityUpvoteRepository extends JpaRepository<CommunityUpvote, UUID> {

    // Check if user has upvoted a topic
    boolean existsByTopicIdAndUserId(UUID topicId, Long userId);

    // Find upvote by topic and user
    Optional<CommunityUpvote> findByTopicIdAndUserId(UUID topicId, Long userId);

    // Count upvotes for a topic
    Long countByTopicId(UUID topicId);
}
