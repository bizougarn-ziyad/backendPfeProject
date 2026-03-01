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
public class SendMessageRequestDto {
    private UUID conversationId;
    private Long recipientId;
    private String content;
    private String messageType;
    private String mediaUrl;
    private String mediaFileName;
    private String mediaMimeType;
}
