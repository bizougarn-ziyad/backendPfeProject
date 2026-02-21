package com.projection.dto.content;

import com.projection.entity.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchedResponseDto {
    private Long tmdbId;
    private ContentType contentType;
    private LocalDateTime watchedAt;
}
