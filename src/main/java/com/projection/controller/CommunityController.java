package com.projection.controller;

import com.projection.dto.community.*;
import com.projection.entity.enums.CommunityCategory;
import com.projection.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/topics")
    public ResponseEntity<TopicDTO> createTopic(
            @Valid @RequestBody CreateTopicRequest request,
            @RequestParam Long userId) {
        TopicDTO topic = communityService.createTopic(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }

    @GetMapping("/topics")
    public ResponseEntity<Page<TopicDTO>> getTopics(
            @RequestParam(required = false) CommunityCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId) {
        Page<TopicDTO> topics = communityService.getTopics(category, page, size, userId);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<TopicDTO> getTopic(
            @PathVariable UUID topicId,
            @RequestParam(required = false) Long userId) {
        TopicDTO topic = communityService.getTopic(topicId, userId);
        return ResponseEntity.ok(topic);
    }

    @PostMapping("/topics/{topicId}/replies")
    public ResponseEntity<ReplyDTO> createReply(
            @PathVariable UUID topicId,
            @Valid @RequestBody CreateReplyRequest request,
            @RequestParam Long userId) {
        ReplyDTO reply = communityService.createReply(topicId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @GetMapping("/topics/{topicId}/replies")
    public ResponseEntity<Page<ReplyDTO>> getReplies(
            @PathVariable UUID topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<ReplyDTO> replies = communityService.getReplies(topicId, page, size);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/topics/{topicId}/upvote")
    public ResponseEntity<Map<String, Object>> toggleUpvote(
            @PathVariable UUID topicId,
            @RequestParam Long userId) {
        boolean hasUpvoted = communityService.toggleUpvote(topicId, userId);
        return ResponseEntity.ok(Map.of(
                "hasUpvoted", hasUpvoted,
                "message", hasUpvoted ? "Topic upvoted" : "Upvote removed"));
    }

    @GetMapping("/categories/stats")
    public ResponseEntity<List<CategoryStatsDTO>> getCategoryStats() {
        List<CategoryStatsDTO> stats = communityService.getCategoryStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TopicDTO>> searchTopics(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId) {
        Page<TopicDTO> topics = communityService.searchTopics(keyword, page, size, userId);
        return ResponseEntity.ok(topics);
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable UUID topicId,
            @RequestParam Long userId) {
        communityService.deleteTopic(topicId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable UUID replyId,
            @RequestParam Long userId) {
        communityService.deleteReply(replyId, userId);
        return ResponseEntity.noContent().build();
    }
}
