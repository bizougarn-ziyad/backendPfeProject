package com.projection.dto.community;

import com.projection.entity.enums.CommunityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStatsDTO {

    private CommunityCategory category;
    private String categoryName;
    private Long topicCount;
}
