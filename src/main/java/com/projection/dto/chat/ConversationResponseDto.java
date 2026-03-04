package com.projection.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponseDto {
    private UUID id;
    private Long otherUserId;
    private String otherUserUsername;
    private String otherUserProfilePicture;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
    private String status;
    private Long createdById;
    private Integer messageCount;
}
