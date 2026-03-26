package com.projection.dto.auth;

import com.projection.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private String country;
    private String profilePictureUrl;
    private Role role;
    private String message;
}
