package com.projection.dto.admin;

import com.projection.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private Boolean isActive;
    private Boolean isSuspended;
    private LocalDateTime suspendedUntil;
    private String banReason;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
