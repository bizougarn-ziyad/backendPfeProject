package com.projection.repository;

import com.projection.entity.community.CommunityTopic;
import com.projection.entity.enums.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityTopicRepository extends JpaRepository<CommunityTopic, UUID> {

    // Find all topics ordered by pinned first, then by last activity
    @Query("SELECT t FROM CommunityTopic t ORDER BY t.isPinned DESC, t.lastActivityAt DESC")
    Page<CommunityTopic> findAllOrderByPinnedAndActivity(Pageable pageable);

    // Find topics by category
    @Query("SELECT t FROM CommunityTopic t WHERE t.category = :category ORDER BY t.isPinned DESC, t.lastActivityAt DESC")
    Page<CommunityTopic> findByCategoryOrderByPinnedAndActivity(@Param("category") CommunityCategory category,
            Pageable pageable);

    // Find topics by author
    Page<CommunityTopic> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    // Get topic count by category
    Long countByCategory(CommunityCategory category);

    // Increment view count
    @Modifying
    @Query("UPDATE CommunityTopic t SET t.viewCount = t.viewCount + 1 WHERE t.id = :topicId")
    void incrementViewCount(@Param("topicId") UUID topicId);

    // Get pinned topics
    List<CommunityTopic> findByIsPinnedTrueOrderByCreatedAtDesc();

    // Search topics
    @Query("SELECT t FROM CommunityTopic t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY t.lastActivityAt DESC")
    Page<CommunityTopic> searchTopics(@Param("keyword") String keyword, Pageable pageable);
}
