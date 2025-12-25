package com.catface996.auth.interfaces.http.controller;

import com.catface996.auth.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for admin-only operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    /**
     * Sample admin-only endpoint for testing RBAC
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Map<String, Object>>> getDashboard(Authentication authentication) {
        log.info("Admin dashboard accessed by: {}", authentication.getName());

        Map<String, Object> dashboardData = Map.of(
                "message", "Welcome to the admin dashboard",
                "user", authentication.getName(),
                "authorities", authentication.getAuthorities().stream()
                        .map(Object::toString)
                        .toList()
        );

        return ResponseEntity.ok(Result.success(dashboardData));
    }

    /**
     * Get system statistics (admin only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Map<String, Object>>> getStats() {
        Map<String, Object> stats = Map.of(
                "status", "healthy",
                "version", "1.0.0"
        );

        return ResponseEntity.ok(Result.success(stats));
    }
}
