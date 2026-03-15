package com.projection.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {

    // ── Existing user/community stats ──────────────────────────────────────
    private long totalUsers;
    private long activeUsers;
    private long suspendedUsers;
    private long bannedUsers;
    private long adminCount;
    private long totalTopics;
    private long totalReplies;
    private long newUsersLast7Days;

    // ── Total watches & ratings ────────────────────────────────────────────
    private long totalWatches;
    private long totalRatings;

    // ── Most-watched content (period-filtered from frontend) ───────────────
    private List<ContentStatDto> topMovies;
    private List<ContentStatDto> topShows;

    // ── Top-rated content ──────────────────────────────────────────────────
    private List<ContentStatDto> topRated;

    // ── Most-collected content ─────────────────────────────────────────────
    private List<ContentStatDto> mostCollected;

    // ── Content type split: {"MOVIE": 1234, "TV_SHOW": 567} ───────────────
    private Map<String, Long> contentTypeSplit;

    // ── Users per country: [{"country":"Algeria","count":42}, ...] ─────────
    private List<CountryStatDto> usersByCountry;

    // ── Signup timeline: last 30 days, one entry per day ──────────────────
    private List<DayStatDto> signupTimeline;

    // ─────────────────────────────────────────────────────────────────────────
    // Nested DTOs
    // ─────────────────────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ContentStatDto {
        private Long tmdbId;
        private String contentType; // "MOVIE" or "TV_SHOW"
        private long count;         // watches / saves / reviews
        private Double avgRating;   // null unless it's a top-rated card
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CountryStatDto {
        private String country;
        private long count;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DayStatDto {
        private String date;   // "2026-03-14"
        private long count;
    }
}
