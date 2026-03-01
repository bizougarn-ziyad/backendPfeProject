package com.projection.dto.review;

import com.projection.entity.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private String id;
    private Long userId;
    private String username;
    private Long tmdbId;
    private ContentType contentType;
    private Integer rating;
    private String reviewText;
    private Integer likesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
