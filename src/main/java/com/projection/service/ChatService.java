package com.projection.service;

import com.projection.dto.chat.ConversationResponseDto;
import com.projection.dto.chat.MessageResponseDto;
import com.projection.dto.chat.SendMessageRequestDto;
import com.projection.entity.enums.ConversationStatus;
import com.projection.entity.messaging.Conversation;
import com.projection.entity.messaging.Message;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ConversationRepository;
import com.projection.repository.MessageRepository;
import com.projection.repository.UserFollowRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    @Transactional
    public MessageResponseDto sendMessage(SendMessageRequestDto request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Conversation conversation;

        if (request.getConversationId() != null) {
            // Existing conversation
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

            // Check if conversation is PENDING and validate message count
            if (conversation.getStatus() == ConversationStatus.PENDING) {
                // If sender is the creator and conversation is pending
                if (conversation.getCreatedBy() != null &&
                        conversation.getCreatedBy().getId().equals(senderId)) {
                    // Check if creator already sent a message
                    Long messageCount = messageRepository.countBySenderIdAndConversationId(
                            conversation.getId(), senderId);
                    if (messageCount > 0) {
                        throw new IllegalStateException(
                                "You can only send one message until the other person accepts your request");
                    }
                } else {
                    // Recipient is responding, accept the conversation
                    conversation.setStatus(ConversationStatus.ACCEPTED);
                    conversationRepository.save(conversation);
                }
            }
        } else if (request.getRecipientId() != null) {
            // Create or find conversation with recipient
            User recipient = userRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

            conversation = conversationRepository.findByTwoUsers(senderId, request.getRecipientId())
                    .orElseGet(() -> {
                        // Check if users follow each other
                        boolean areMutualFollowers = userFollowRepository.areMutualFollowers(
                                senderId, request.getRecipientId());

                        ConversationStatus status = areMutualFollowers ? ConversationStatus.ACCEPTED
                                : ConversationStatus.PENDING;

                        Conversation newConv = Conversation.builder()
                                .isGroup(false)
                                .status(status)
                                .createdBy(sender)
                                .participants(new HashSet<>(Arrays.asList(sender, recipient)))
                                .messages(new ArrayList<>())
                                .build();
                        return conversationRepository.save(newConv);
                    });
        } else {
            throw new IllegalArgumentException("Either conversationId or recipientId must be provided");
        }

        // Create and save message
        Message.MessageBuilder messageBuilder = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .isRead(false)
                .isDeleted(false);

        // Handle message type and media
        if (request.getMessageType() != null) {
            try {
                messageBuilder
                        .messageType(com.projection.entity.messaging.MessageType.valueOf(request.getMessageType()));
            } catch (IllegalArgumentException e) {
                messageBuilder.messageType(com.projection.entity.messaging.MessageType.TEXT);
            }
        }

        if (request.getMediaUrl() != null) {
            messageBuilder.mediaUrl(request.getMediaUrl())
                    .mediaFileName(request.getMediaFileName())
                    .mediaMimeType(request.getMediaMimeType());
        }

        Message message = messageBuilder.build();
        message = messageRepository.save(message);

        return mapToMessageResponseDto(message);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getUserConversations(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        List<Conversation> conversations = conversationRepository.findAllByUserId(userId);

        return conversations.stream()
                .map(conv -> mapToConversationResponseDto(conv, userId))
                .sorted((c1, c2) -> {
                    if (c1.getLastMessageTime() == null)
                        return 1;
                    if (c2.getLastMessageTime() == null)
                        return -1;
                    return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getConversationMessages(UUID conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Verify user is part of conversation
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));

        if (!isParticipant) {
            throw new IllegalArgumentException("User is not part of this conversation");
        }

        List<Message> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);

        return messages.stream()
                .map(this::mapToMessageResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(UUID conversationId, Long userId) {
        List<Message> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);

        messages.stream()
                .filter(m -> !m.getSender().getId().equals(userId) && !m.getIsRead())
                .forEach(m -> m.setIsRead(true));

        messageRepository.saveAll(messages);
    }

    @Transactional(readOnly = true)
    public Long getUnreadMessagesCount(Long userId) {
        return messageRepository.countUnreadMessagesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Long getRecipientId(UUID conversationId, Long senderId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Get the other participant who is not the sender
        return conversation.getParticipants().stream()
                .filter(p -> !p.getId().equals(senderId))
                .findFirst()
                .map(User::getId)
                .orElse(null);
    }

    private MessageResponseDto mapToMessageResponseDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .senderProfilePicture(message.getSender().getProfilePictureUrl())
                .content(message.getContent())
                .messageType(message.getMessageType().name())
                .mediaUrl(message.getMediaUrl())
                .mediaFileName(message.getMediaFileName())
                .mediaMimeType(message.getMediaMimeType())
                .isRead(message.getIsRead())
                .sentAt(message.getSentAt())
                .build();
    }

    private ConversationResponseDto mapToConversationResponseDto(Conversation conversation, Long currentUserId) {
        // Get the other user in the conversation
        User otherUser = conversation.getParticipants().stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        // Get last message
        List<Message> messages = conversation.getMessages().stream()
                .filter(m -> !m.getIsDeleted())
                .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
                .toList();

        Message lastMessage = messages.isEmpty() ? null : messages.get(0);

        // Count unread messages
        Long unreadCount = messageRepository.countUnreadMessagesByConversationIdAndUserId(
                conversation.getId(), currentUserId);

        // Count total messages
        Integer messageCount = messageRepository.countByConversationId(conversation.getId());

        return ConversationResponseDto.builder()
                .id(conversation.getId())
                .otherUserId(otherUser != null ? otherUser.getId() : null)
                .otherUserUsername(otherUser != null ? otherUser.getUsername() : "Unknown")
                .otherUserProfilePicture(otherUser != null ? otherUser.getProfilePictureUrl() : null)
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : conversation.getCreatedAt())
                .unreadCount(unreadCount)
                .status(conversation.getStatus() != null ? conversation.getStatus().name() : "ACCEPTED")
                .createdById(conversation.getCreatedBy() != null ? conversation.getCreatedBy().getId() : null)
                .messageCount(messageCount)
                .build();
    }
}
