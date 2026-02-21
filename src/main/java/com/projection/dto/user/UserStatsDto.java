package com.projection.dto.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    private long moviesWatched;
    private long seriesWatched;
    private long following;
    private long followers;
    private long reviewsCount;
    private long listsCount;
}
