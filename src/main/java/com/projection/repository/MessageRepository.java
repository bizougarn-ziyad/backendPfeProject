package com.projection.repository;

import com.projection.entity.messaging.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.isDeleted = false ORDER BY m.sentAt ASC")
    List<Message> findByConversationIdOrderBySentAtAsc(@Param("conversationId") UUID conversationId);

    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c JOIN c.participants p " +
            "WHERE p.id = :userId AND m.sender.id != :userId AND m.isRead = false AND m.isDeleted = false")
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId AND m.isRead = false AND m.isDeleted = false")
    Long countUnreadMessagesByConversationIdAndUserId(@Param("conversationId") UUID conversationId,
            @Param("userId") Long userId);
}
