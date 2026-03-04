package com.projection.repository;

import com.projection.entity.community.CommunityReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityReplyRepository extends JpaRepository<CommunityReply, UUID> {

    // Find replies by topic
    @Query("SELECT r FROM CommunityReply r WHERE r.topic.id = :topicId AND r.isDeleted = false ORDER BY r.createdAt ASC")
    Page<CommunityReply> findByTopicIdOrderByCreatedAtAsc(@Param("topicId") UUID topicId, Pageable pageable);

    // Find replies by author
    Page<CommunityReply> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    // Count replies for a topic
    Long countByTopicIdAndIsDeletedFalse(UUID topicId);

    // Get latest reply for a topic
    @Query("SELECT r FROM CommunityReply r WHERE r.topic.id = :topicId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<CommunityReply> findLatestByTopicId(@Param("topicId") UUID topicId, Pageable pageable);
}
