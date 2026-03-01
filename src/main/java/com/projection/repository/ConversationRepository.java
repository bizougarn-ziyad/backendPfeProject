package com.projection.repository;

import com.projection.entity.messaging.Conversation;
import com.projection.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId AND c.isGroup = false ORDER BY c.createdAt DESC")
    List<Conversation> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 " +
            "WHERE p1.id = :user1Id AND p2.id = :user2Id AND c.isGroup = false")
    Optional<Conversation> findByTwoUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT DISTINCT u FROM Conversation c JOIN c.participants u WHERE c.id = :conversationId")
    List<User> findParticipantsByConversationId(@Param("conversationId") UUID conversationId);
}
