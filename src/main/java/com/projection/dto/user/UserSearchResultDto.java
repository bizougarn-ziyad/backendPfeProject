package com.projection.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchResultDto {
    private Long id;
    private String username;
    private String bio;
    private String profilePictureUrl;
}
