package com.projection.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListResponseDto {

    private String id;
    private String name;
    private String description;
    private Boolean isPublic;
    private int itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ListItemResponseDto> items;
}
