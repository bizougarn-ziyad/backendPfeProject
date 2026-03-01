package com.projection.controller;

import com.projection.dto.chat.ChatNotificationDto;
import com.projection.dto.chat.MessageResponseDto;
import com.projection.dto.chat.SendMessageRequestDto;
import com.projection.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequestDto request) {
        try {
            MessageResponseDto savedMessage = chatService.sendMessage(request, request.getRecipientId());

            // Send notification to recipient
            ChatNotificationDto notification = ChatNotificationDto.builder()
                    .messageId(savedMessage.getId())
                    .conversationId(savedMessage.getConversationId())
                    .senderId(savedMessage.getSenderId())
                    .senderUsername(savedMessage.getSenderUsername())
                    .content(savedMessage.getContent())
                    .build();

            messagingTemplate.convertAndSendToUser(
                    request.getRecipientId().toString(),
                    "/queue/messages",
                    notification);

        } catch (Exception e) {
            // Log error
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
