package com.projection.service;

import com.projection.dto.community.*;
import com.projection.entity.community.CommunityReply;
import com.projection.entity.community.CommunityTopic;
import com.projection.entity.community.CommunityUpvote;
import com.projection.entity.enums.CommunityCategory;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.CommunityReplyRepository;
import com.projection.repository.CommunityTopicRepository;
import com.projection.repository.CommunityUpvoteRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityTopicRepository topicRepository;
    private final CommunityReplyRepository replyRepository;
    private final CommunityUpvoteRepository upvoteRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TopicDTO createTopic(CreateTopicRequest request, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CommunityTopic topic = CommunityTopic.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .author(author)
                .build();

        topic = topicRepository.save(topic);

        // Broadcast new topic via WebSocket
        messagingTemplate.convertAndSend("/topic/community/new", convertToDTO(topic, null));

        return convertToDTO(topic, userId);
    }

    @Transactional(readOnly = true)
    public Page<TopicDTO> getTopics(CommunityCategory category, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityTopic> topics;

        if (category != null) {
            topics = topicRepository.findByCategoryOrderByPinnedAndActivity(category, pageable);
        } else {
            topics = topicRepository.findAllOrderByPinnedAndActivity(pageable);
        }

        return topics.map(topic -> convertToDTO(topic, currentUserId));
    }

    @Transactional
    public TopicDTO getTopic(UUID topicId, Long currentUserId) {
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        // Increment view count
        topicRepository.incrementViewCount(topicId);
        topic.setViewCount(topic.getViewCount() + 1);

        return convertToDTO(topic, currentUserId);
    }

    @Transactional
    public ReplyDTO createReply(UUID topicId, CreateReplyRequest request, Long userId) {
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        if (topic.getIsLocked()) {
            throw new IllegalStateException("This topic is locked and cannot accept new replies");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CommunityReply.CommunityReplyBuilder replyBuilder = CommunityReply.builder()
                .topic(topic)
                .author(author)
                .content(request.getContent());

        // Handle nested reply (reply to a reply)
        if (request.getParentReplyId() != null) {
            CommunityReply parentReply = replyRepository.findById(request.getParentReplyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent reply not found"));
            replyBuilder.parentReply(parentReply);
        }

        CommunityReply reply = replyBuilder.build();
        reply = replyRepository.save(reply);

        // Update topic reply count and last activity
        topic.setReplyCount(topic.getReplyCount() + 1);
        topic.setLastActivityAt(LocalDateTime.now());
        topicRepository.save(topic);

        ReplyDTO replyDTO = convertToReplyDTO(reply);

        // Broadcast new reply via WebSocket
        messagingTemplate.convertAndSend("/topic/community/topic/" + topicId + "/reply", replyDTO);

        return replyDTO;
    }

    @Transactional(readOnly = true)
    public Page<ReplyDTO> getReplies(UUID topicId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityReply> replies = replyRepository.findByTopicIdOrderByCreatedAtAsc(topicId, pageable);
        return replies.map(this::convertToReplyDTO);
    }

    @Transactional
    public boolean toggleUpvote(UUID topicId, Long userId) {
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean hasUpvoted = upvoteRepository.existsByTopicIdAndUserId(topicId, userId);

        if (hasUpvoted) {
            // Remove upvote
            CommunityUpvote upvote = upvoteRepository.findByTopicIdAndUserId(topicId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Upvote not found"));
            upvoteRepository.delete(upvote);
            topic.setUpvoteCount(Math.max(0, topic.getUpvoteCount() - 1));
            topicRepository.save(topic);

            // Broadcast upvote count update
            messagingTemplate.convertAndSend("/topic/community/topic/" + topicId + "/upvotes",
                    topic.getUpvoteCount());

            return false;
        } else {
            // Add upvote
            CommunityUpvote upvote = CommunityUpvote.builder()
                    .topic(topic)
                    .user(user)
                    .build();
            upvoteRepository.save(upvote);
            topic.setUpvoteCount(topic.getUpvoteCount() + 1);
            topicRepository.save(topic);

            // Broadcast upvote count update
            messagingTemplate.convertAndSend("/topic/community/topic/" + topicId + "/upvotes",
                    topic.getUpvoteCount());

            return true;
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryStatsDTO> getCategoryStats() {
        return Arrays.stream(CommunityCategory.values())
                .map(category -> CategoryStatsDTO.builder()
                        .category(category)
                        .categoryName(getCategoryDisplayName(category))
                        .topicCount(topicRepository.countByCategory(category))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TopicDTO> searchTopics(String keyword, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityTopic> topics = topicRepository.searchTopics(keyword, pageable);
        return topics.map(topic -> convertToDTO(topic, currentUserId));
    }

    @Transactional
    public void deleteTopic(UUID topicId, Long userId) {
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        // Only the author can delete the topic
        if (!topic.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own topics");
        }

        topicRepository.delete(topic);
    }

    @Transactional
    public void deleteReply(UUID replyId, Long userId) {
        CommunityReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found"));

        // Only the author can delete the reply
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own replies");
        }

        reply.setIsDeleted(true);
        replyRepository.save(reply);

        // Update topic reply count
        CommunityTopic topic = reply.getTopic();
        topic.setReplyCount(Math.max(0, topic.getReplyCount() - 1));
        topicRepository.save(topic);
    }

    private TopicDTO convertToDTO(CommunityTopic topic, Long currentUserId) {
        boolean hasUpvoted = currentUserId != null &&
                upvoteRepository.existsByTopicIdAndUserId(topic.getId(), currentUserId);

        return TopicDTO.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .content(topic.getContent())
                .category(topic.getCategory())
                .categoryName(getCategoryDisplayName(topic.getCategory()))
                .author(TopicDTO.AuthorDTO.builder()
                        .id(topic.getAuthor().getId())
                        .username(topic.getAuthor().getUsername())
                        .profilePictureUrl(topic.getAuthor().getProfilePictureUrl())
                        .build())
                .isPinned(topic.getIsPinned())
                .isLocked(topic.getIsLocked())
                .upvoteCount(topic.getUpvoteCount())
                .replyCount(topic.getReplyCount())
                .viewCount(topic.getViewCount())
                .hasUpvoted(hasUpvoted)
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .lastActivityAt(topic.getLastActivityAt())
                .build();
    }

    private ReplyDTO convertToReplyDTO(CommunityReply reply) {
        ReplyDTO.ReplyDTOBuilder builder = ReplyDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .author(ReplyDTO.AuthorDTO.builder()
                        .id(reply.getAuthor().getId())
                        .username(reply.getAuthor().getUsername())
                        .profilePictureUrl(reply.getAuthor().getProfilePictureUrl())
                        .build())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt());

        // Include parent reply info if this is a nested reply
        if (reply.getParentReply() != null) {
            CommunityReply parent = reply.getParentReply();
            builder.parentReply(ReplyDTO.ParentReplyDTO.builder()
                    .id(parent.getId())
                    .authorUsername(parent.getAuthor().getUsername())
                    .content(parent.getContent().length() > 100
                            ? parent.getContent().substring(0, 100) + "..."
                            : parent.getContent())
                    .build());
        }

        return builder.build();
    }

    private String getCategoryDisplayName(CommunityCategory category) {
        return switch (category) {
            case ANNOUNCEMENTS -> "Announcements";
            case MOVIES -> "Movie Talk";
            case TV_SHOWS -> "TV Shows";
            case RECOMMENDATIONS -> "Recommendations";
            case SUGGESTIONS -> "Feature Requests";
            case SUPPORT -> "Help & Support";
        };
    }
}
