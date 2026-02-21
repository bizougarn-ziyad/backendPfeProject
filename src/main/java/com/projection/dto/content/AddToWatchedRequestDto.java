package com.projection.dto.content;

import com.projection.entity.enums.ContentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToWatchedRequestDto {
    @NotNull(message = "TMDB ID is required")
    private Long tmdbId;

    @NotNull(message = "Content type is required")
    private ContentType contentType;
}
