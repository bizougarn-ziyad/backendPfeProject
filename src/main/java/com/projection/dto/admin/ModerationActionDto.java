package com.projection.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationActionDto {

    private Long userId;
    private String username;
    /** SUSPENDED, BANNED, UNBANNED */
    private String action;
    private String reason;
    private LocalDateTime suspendedUntil;
    private Long performedByAdminId;
}
