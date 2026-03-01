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
public class MessageResponseDto {
    private UUID id;
    private UUID conversationId;
    private Long senderId;
    private String senderUsername;
    private String senderProfilePicture;
    private String content;
    private String messageType;
    private String mediaUrl;
    private String mediaFileName;
    private String mediaMimeType;
    private Boolean isRead;
    private LocalDateTime sentAt;
}
