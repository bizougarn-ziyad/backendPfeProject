package com.projection.dto.rating;

import com.projection.entity.enums.ContentType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRatingRequestDto {

    @NotNull(message = "TMDB ID is required")
    private Integer tmdbId;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating; // 0 to remove rating, 0.5 to 5.0 in 0.5 increments
}
