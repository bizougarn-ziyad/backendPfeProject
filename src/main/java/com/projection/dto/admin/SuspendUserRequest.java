package com.projection.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspendUserRequest {

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotNull(message = "Hours is required")
    @Min(value = 1, message = "Suspension must be at least 1 hour")
    private Integer hours;
}
