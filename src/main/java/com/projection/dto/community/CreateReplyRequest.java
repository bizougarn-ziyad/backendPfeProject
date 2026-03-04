package com.projection.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReplyRequest {

    @NotBlank(message = "Content is required")
    @Size(min = 1, message = "Reply cannot be empty")
    private String content;

    private UUID parentReplyId; // Optional: for nested replies
}
