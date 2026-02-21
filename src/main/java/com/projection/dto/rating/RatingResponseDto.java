package com.projection.dto.rating;

import com.projection.entity.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {

    private Long id;
    private Long userId;
    private Integer tmdbId;
    private ContentType contentType;
    private String contentTitle;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
