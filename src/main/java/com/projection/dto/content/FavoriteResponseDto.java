package com.projection.dto.content;

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
public class FavoriteResponseDto {
    private String id;
    private Long tmdbId;
    private ContentType contentType;
    private LocalDateTime createdAt;
}
