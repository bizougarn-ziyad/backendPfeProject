package com.projection.controller;

import com.projection.dto.community.CreateReplyRequest;
import com.projection.dto.community.ReplyDTO;
import com.projection.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebSocketCommunityController {

    private final CommunityService communityService;

    @MessageMapping("/community/topic/{topicId}/reply")
    public void sendReply(
            @DestinationVariable String topicId,
            @Payload Map<String, Object> payload,
            SimpMessageHeaderAccessor headerAccessor) {

        Long userId = Long.parseLong(payload.get("userId").toString());
        String content = payload.get("content").toString();

        CreateReplyRequest request = CreateReplyRequest.builder()
                .content(content)
                .build();

        // Support nested replies (reply to a reply)
        if (payload.containsKey("parentReplyId") && payload.get("parentReplyId") != null) {
            request.setParentReplyId(java.util.UUID.fromString(payload.get("parentReplyId").toString()));
        }

        // Service will handle saving + WebSocket broadcast
        communityService.createReply(UUID.fromString(topicId), request, userId);
    }

    @MessageMapping("/community/topic/{topicId}/upvote")
    public void toggleUpvote(
            @DestinationVariable String topicId,
            @Payload Map<String, Object> payload) {

        Long userId = Long.parseLong(payload.get("userId").toString());

        // Service will handle the WebSocket broadcast
        communityService.toggleUpvote(UUID.fromString(topicId), userId);
    }
}
