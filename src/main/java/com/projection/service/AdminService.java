package com.projection.service;

import com.projection.dto.admin.*;
import com.projection.entity.community.CommunityTopic;
import com.projection.entity.enums.Role;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final CommunityTopicRepository topicRepository;
    private final CommunityReplyRepository replyRepository;
    private final UserWatchedRepository watchedRepository;
    private final UserRatingRepository ratingRepository;
    private final ListItemRepository listItemRepository;

    private final PasswordEncoder passwordEncoder;
    private final SimpMessagingTemplate messagingTemplate;

    // ─────────────────────────────────────────────────────────────────────────
    // Guard
    // ─────────────────────────────────────────────────────────────────────────

    private User verifyAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new SecurityException("Access denied: user is not an admin");
        }
        return admin;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Feature 1 – User management
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public AdminUserDto addAdmin(Long adminId, CreateAdminRequest request) {
        verifyAdmin(adminId);
        log.info("Admin {} creating new admin user: {}", adminId, request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        User newAdmin = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .isActive(true)
                .isSuspended(false)
                .build();

        newAdmin = userRepository.save(newAdmin);
        log.info("New admin created with ID: {}", newAdmin.getId());
        return toAdminUserDto(newAdmin);
    }

    @Transactional(readOnly = true)
    public List<AdminUserDto> getAllUsers(Long adminId) {
        verifyAdmin(adminId);
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toAdminUserDto)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Feature 2 – Community moderation
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public AdminUserDto suspendUser(Long adminId, Long targetUserId, SuspendUserRequest request) {
        verifyAdmin(adminId);
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        target.setIsSuspended(true);
        target.setSuspendedUntil(LocalDateTime.now().plusHours(request.getHours()));
        target.setBanReason(request.getReason());
        userRepository.save(target);

        log.info("Admin {} suspended user {} for {} hours. Reason: {}",
                adminId, targetUserId, request.getHours(), request.getReason());

        // Broadcast moderation action via WebSocket
        ModerationActionDto action = ModerationActionDto.builder()
                .userId(target.getId())
                .username(target.getUsername())
                .action("SUSPENDED")
                .reason(request.getReason())
                .suspendedUntil(target.getSuspendedUntil())
                .performedByAdminId(adminId)
                .build();
        messagingTemplate.convertAndSend("/topic/admin/moderation", action);
        // Personal alert to the suspended user
        Object suspendAlert = java.util.Map.of(
                "action", "SUSPENDED",
                "reason", request.getReason(),
                "suspendedUntil", target.getSuspendedUntil().toString(),
                "message", "Your account has been temporarily suspended."
        );
        messagingTemplate.convertAndSend("/topic/moderation-alert/" + target.getId(), suspendAlert);

        return toAdminUserDto(target);
    }

    @Transactional
    public AdminUserDto banUser(Long adminId, Long targetUserId, BanUserRequest request) {
        verifyAdmin(adminId);
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        target.setIsActive(false);
        target.setIsSuspended(false);
        target.setBanReason(request.getReason());
        userRepository.save(target);

        log.info("Admin {} permanently banned user {}. Reason: {}", adminId, targetUserId, request.getReason());

        ModerationActionDto action = ModerationActionDto.builder()
                .userId(target.getId())
                .username(target.getUsername())
                .action("BANNED")
                .reason(request.getReason())
                .performedByAdminId(adminId)
                .build();
        messagingTemplate.convertAndSend("/topic/admin/moderation", action);
        // Personal alert to the banned user
        Object banAlert = java.util.Map.of(
                "action", "BANNED",
                "reason", request.getReason(),
                "message", "Your account has been permanently banned."
        );
        messagingTemplate.convertAndSend("/topic/moderation-alert/" + target.getId(), banAlert);

        return toAdminUserDto(target);
    }

    @Transactional
    public AdminUserDto unbanUser(Long adminId, Long targetUserId) {
        verifyAdmin(adminId);
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        target.setIsActive(true);
        target.setIsSuspended(false);
        target.setSuspendedUntil(null);
        target.setBanReason(null);
        userRepository.save(target);

        log.info("Admin {} lifted restrictions on user {}", adminId, targetUserId);

        ModerationActionDto action = ModerationActionDto.builder()
                .userId(target.getId())
                .username(target.getUsername())
                .action("UNBANNED")
                .performedByAdminId(adminId)
                .build();
        messagingTemplate.convertAndSend("/topic/admin/moderation", action);
        // Personal alert to the user that their restrictions are lifted
        Object liftAlert = java.util.Map.of(
                "action", "UNBANNED",
                "message", "Your account restrictions have been lifted. You can post again."
        );
        messagingTemplate.convertAndSend("/topic/moderation-alert/" + target.getId(), liftAlert);

        return toAdminUserDto(target);
    }

    @Transactional
    public void forceDeleteTopic(Long adminId, UUID topicId) {
        verifyAdmin(adminId);
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        topicRepository.delete(topic);
        log.info("Admin {} force-deleted topic {}", adminId, topicId);
    }

    @Transactional
    public void forceDeleteReply(Long adminId, UUID replyId) {
        verifyAdmin(adminId);
        com.projection.entity.community.CommunityReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found: " + replyId));
        replyRepository.delete(reply);
        log.info("Admin {} force-deleted reply {}", adminId, replyId);
    }

    @Transactional
    public void setTopicLocked(Long adminId, UUID topicId, boolean locked) {
        verifyAdmin(adminId);
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        topic.setIsLocked(locked);
        topicRepository.save(topic);
        log.info("Admin {} {} topic {}", adminId, locked ? "locked" : "unlocked", topicId);

        // Notify community subscribers in real-time
        Object lockPayload = java.util.Map.of("topicId", topicId.toString(), "isLocked", locked, "action", locked ? "LOCKED" : "UNLOCKED");
        messagingTemplate.convertAndSend("/topic/community/topic/" + topicId + "/moderation", lockPayload);
    }

    @Transactional
    public void setTopicPinned(Long adminId, UUID topicId, boolean pinned) {
        verifyAdmin(adminId);
        CommunityTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
        topic.setIsPinned(pinned);
        topicRepository.save(topic);
        log.info("Admin {} {} topic {}", adminId, pinned ? "pinned" : "unpinned", topicId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Feature 3 – Dashboard
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats(Long adminId) {
        return getDashboardStats(adminId, "all");
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats(Long adminId, String period) {
        verifyAdmin(adminId);

        LocalDateTime sevenDaysAgo  = LocalDateTime.now().minusDays(7);
        LocalDateTime since = switch (period) {
            case "day"   -> LocalDateTime.now().minusDays(1);
            case "week"  -> LocalDateTime.now().minusDays(7);
            case "month" -> LocalDateTime.now().minusDays(30);
            default      -> LocalDateTime.of(1970, 1, 1, 0, 0); // all-time date to avoid PG null param errors
        };

        // ── Core counters ────────────────────────────────────────────────
        long totalUsers       = userRepository.count();
        long activeUsers      = userRepository.countByIsActiveTrue();
        long suspendedUsers   = userRepository.countByIsSuspendedTrue();
        long bannedUsers      = userRepository.countByIsActiveFalse();
        long adminCount       = userRepository.countByRole(Role.ADMIN);
        long totalTopics      = topicRepository.count();
        long totalReplies     = replyRepository.count();
        long newUsersLast7Days = userRepository.countByCreatedAtAfter(sevenDaysAgo);
        long totalWatches     = watchedRepository.count();
        long totalRatings     = ratingRepository.count();

        PageRequest top10 = PageRequest.of(0, 10);
        PageRequest top5  = PageRequest.of(0, 5);

        // ── Top movies & shows ───────────────────────────────────────────
        List<AdminDashboardDto.ContentStatDto> topMovies = watchedRepository
                .findTopWatched(since, "MOVIE", top10)
                .stream()
                .map(r -> AdminDashboardDto.ContentStatDto.builder()
                        .tmdbId(safeLong(r[0]))
                        .contentType(safeString(r[1]))
                        .count(safeLong(r[2]))
                        .build())
                .collect(Collectors.toList());

        List<AdminDashboardDto.ContentStatDto> topShows = watchedRepository
                .findTopWatched(since, "TV", top10)
                .stream()
                .map(r -> AdminDashboardDto.ContentStatDto.builder()
                        .tmdbId(safeLong(r[0]))
                        .contentType(safeString(r[1]))
                        .count(safeLong(r[2]))
                        .build())
                .collect(Collectors.toList());

        // ── Top rated ────────────────────────────────────────────────────
        List<AdminDashboardDto.ContentStatDto> topRated = ratingRepository
                .findTopRated(top10)
                .stream()
                .map(r -> AdminDashboardDto.ContentStatDto.builder()
                        .tmdbId(safeLong(r[0]))
                        .contentType(safeString(r[1]))
                        .avgRating(safeDouble(r[2]))
                        .count(safeLong(r[3]))
                        .build())
                .collect(Collectors.toList());

        // ── Most collected ───────────────────────────────────────────────
        List<AdminDashboardDto.ContentStatDto> mostCollected = listItemRepository
                .findMostCollected(top10)
                .stream()
                .map(r -> AdminDashboardDto.ContentStatDto.builder()
                        .tmdbId(safeLong(r[0]))
                        .contentType(safeString(r[1]))
                        .count(safeLong(r[2]))
                        .build())
                .collect(Collectors.toList());

        // ── Content type split ───────────────────────────────────────────
        Map<String, Long> contentTypeSplit = new LinkedHashMap<>();
        watchedRepository.countByContentType()
                .forEach(r -> contentTypeSplit.put(safeString(r[0]), safeLong(r[1])));

        // ── Users by country ─────────────────────────────────────────────
        List<AdminDashboardDto.CountryStatDto> usersByCountry = userRepository
                .countByCountry()
                .stream()
                .map(r -> AdminDashboardDto.CountryStatDto.builder()
                        .country(safeString(r[0]))
                        .count(safeLong(r[1]))
                        .build())
                .collect(Collectors.toList());

        // ── Signup timeline (last 30 days, grouped by day) ───────────────
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<User> recentUsers = userRepository.findCreatedSince(thirtyDaysAgo);
        Map<String, Long> byDay = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Pre-fill all 30 days with 0
        for (int i = 29; i >= 0; i--) {
            byDay.put(LocalDate.now().minusDays(i).format(fmt), 0L);
        }
        recentUsers.forEach(u -> {
            String day = u.getCreatedAt().toLocalDate().format(fmt);
            byDay.merge(day, 1L, Long::sum);
        });
        List<AdminDashboardDto.DayStatDto> signupTimeline = byDay.entrySet().stream()
                .map(e -> AdminDashboardDto.DayStatDto.builder().date(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .suspendedUsers(suspendedUsers)
                .bannedUsers(bannedUsers)
                .adminCount(adminCount)
                .totalTopics(totalTopics)
                .totalReplies(totalReplies)
                .newUsersLast7Days(newUsersLast7Days)
                .totalWatches(totalWatches)
                .totalRatings(totalRatings)
                .topMovies(topMovies)
                .topShows(topShows)
                .topRated(topRated)
                .mostCollected(mostCollected)
                .contentTypeSplit(contentTypeSplit)
                .usersByCountry(usersByCountry)
                .signupTimeline(signupTimeline)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private AdminUserDto toAdminUserDto(User user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isSuspended(user.getIsSuspended())
                .suspendedUntil(user.getSuspendedUntil())
                .banReason(user.getBanReason())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    private Long safeLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return 0L;
    }

    private Double safeDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return 0.0;
    }

    private String safeString(Object obj) {
        if (obj == null) return "Unknown";
        return obj.toString();
    }
}
