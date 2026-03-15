package com.projection.controller;

import com.projection.dto.admin.*;
import com.projection.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminController {

    private final AdminService adminService;

    // ─────────────────────────────────────────────────────────────────────────
    // Dashboard
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard(
            @RequestParam Long adminId,
            @RequestParam(defaultValue = "all") String period) {
        log.info("Admin {} requesting dashboard stats (period={})", adminId, period);
        return ResponseEntity.ok(adminService.getDashboardStats(adminId, period));
    }


    // ─────────────────────────────────────────────────────────────────────────
    // User management
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDto>> getAllUsers(@RequestParam Long adminId) {
        log.info("Admin {} fetching all users", adminId);
        return ResponseEntity.ok(adminService.getAllUsers(adminId));
    }

    @PostMapping("/users")
    public ResponseEntity<AdminUserDto> addAdmin(
            @RequestParam Long adminId,
            @Valid @RequestBody CreateAdminRequest request) {
        log.info("Admin {} creating new admin: {}", adminId, request.getEmail());
        AdminUserDto newAdmin = adminService.addAdmin(adminId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Community moderation
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/users/{targetUserId}/suspend")
    public ResponseEntity<AdminUserDto> suspendUser(
            @PathVariable Long targetUserId,
            @RequestParam Long adminId,
            @Valid @RequestBody SuspendUserRequest request) {
        log.info("Admin {} suspending user {}", adminId, targetUserId);
        return ResponseEntity.ok(adminService.suspendUser(adminId, targetUserId, request));
    }

    @PutMapping("/users/{targetUserId}/ban")
    public ResponseEntity<AdminUserDto> banUser(
            @PathVariable Long targetUserId,
            @RequestParam Long adminId,
            @Valid @RequestBody BanUserRequest request) {
        log.info("Admin {} banning user {}", adminId, targetUserId);
        return ResponseEntity.ok(adminService.banUser(adminId, targetUserId, request));
    }

    @PutMapping("/users/{targetUserId}/unban")
    public ResponseEntity<AdminUserDto> unbanUser(
            @PathVariable Long targetUserId,
            @RequestParam Long adminId) {
        log.info("Admin {} unbanning user {}", adminId, targetUserId);
        return ResponseEntity.ok(adminService.unbanUser(adminId, targetUserId));
    }

    @DeleteMapping("/community/topics/{topicId}")
    public ResponseEntity<Void> forceDeleteTopic(
            @PathVariable UUID topicId,
            @RequestParam Long adminId) {
        log.info("Admin {} force-deleting topic {}", adminId, topicId);
        adminService.forceDeleteTopic(adminId, topicId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/community/replies/{replyId}")
    public ResponseEntity<Void> forceDeleteReply(
            @PathVariable UUID replyId,
            @RequestParam Long adminId) {
        log.info("Admin {} force-deleting reply {}", adminId, replyId);
        adminService.forceDeleteReply(adminId, replyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/community/topics/{topicId}/lock")
    public ResponseEntity<Map<String, Object>> lockTopic(
            @PathVariable UUID topicId,
            @RequestParam Long adminId,
            @RequestParam boolean locked) {
        log.info("Admin {} setting topic {} lock={}", adminId, topicId, locked);
        adminService.setTopicLocked(adminId, topicId, locked);
        return ResponseEntity.ok(Map.of("topicId", topicId.toString(), "isLocked", locked));
    }

    @PutMapping("/community/topics/{topicId}/pin")
    public ResponseEntity<Map<String, Object>> pinTopic(
            @PathVariable UUID topicId,
            @RequestParam Long adminId,
            @RequestParam boolean pinned) {
        log.info("Admin {} setting topic {} pinned={}", adminId, topicId, pinned);
        adminService.setTopicPinned(adminId, topicId, pinned);
        return ResponseEntity.ok(Map.of("topicId", topicId.toString(), "isPinned", pinned));
    }
}
