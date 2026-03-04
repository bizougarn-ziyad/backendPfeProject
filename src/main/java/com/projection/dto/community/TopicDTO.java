package com.projection.dto.community;

import com.projection.entity.enums.CommunityCategory;
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
public class TopicDTO {

    private UUID id;
    private String title;
    private String content;
    private CommunityCategory category;
    private String categoryName;

    private AuthorDTO author;

    private Boolean isPinned;
    private Boolean isLocked;
    private Integer upvoteCount;
    private Integer replyCount;
    private Integer viewCount;

    private Boolean hasUpvoted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorDTO {
        private Long id;
        private String username;
        private String profilePictureUrl;
    }
}
