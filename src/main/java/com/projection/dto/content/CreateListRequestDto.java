package com.projection.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateListRequestDto {

    @NotBlank(message = "List name is required")
    @Size(max = 255, message = "List name must not exceed 255 characters")
    private String name;

    private String description;

    @Builder.Default
    private Boolean isPublic = false;
}
