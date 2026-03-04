package com.projection.controller;

import com.projection.dto.chat.ChatNotificationDto;
import com.projection.dto.chat.ConversationResponseDto;
import com.projection.dto.chat.MessageResponseDto;
import com.projection.dto.chat.SendMessageRequestDto;
import com.projection.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDto>> getUserConversations(
            @RequestParam Long userId) {
        List<ConversationResponseDto> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponseDto>> getConversationMessages(
            @PathVariable UUID conversationId,
            @RequestParam Long userId) {
        List<MessageResponseDto> messages = chatService.getConversationMessages(conversationId, userId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages/send")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @RequestBody SendMessageRequestDto request,
            @RequestParam Long userId) {
        MessageResponseDto message = chatService.sendMessage(request, userId);

        // Determine recipient ID and send WebSocket notification
        Long recipientId = chatService.getRecipientId(message.getConversationId(), userId);
        if (recipientId != null) {
            ChatNotificationDto notification = ChatNotificationDto.builder()
                    .messageId(message.getId())
                    .conversationId(message.getConversationId())
                    .senderId(message.getSenderId())
                    .senderUsername(message.getSenderUsername())
                    .content(message.getContent())
                    .build();

            messagingTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/messages",
                    notification);
        }

        return ResponseEntity.ok(message);
    }

    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable UUID conversationId,
            @RequestParam Long userId) {
        chatService.markMessagesAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessagesCount(@RequestParam Long userId) {
        Long count = chatService.getUnreadMessagesCount(userId);
        return ResponseEntity.ok(count);
    }
}
