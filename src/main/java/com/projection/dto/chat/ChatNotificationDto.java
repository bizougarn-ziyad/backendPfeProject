package com.projection.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotificationDto {
    private UUID messageId;
    private UUID conversationId;
    private Long senderId;
    private String senderUsername;
    private String content;
}
