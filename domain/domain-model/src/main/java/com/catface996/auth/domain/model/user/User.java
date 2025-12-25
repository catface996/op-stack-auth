package com.catface996.auth.domain.model.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User domain entity - aggregate root
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private UserStatus status;
    private Integer failedAttempts;
    private LocalDateTime lockedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> roles;

    /**
     * Check if account is currently locked
     */
    public boolean isLocked() {
        if (status == UserStatus.LOCKED) {
            if (lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
                // Auto-unlock after lockout period
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Check if account is active and can login
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE && !isLocked();
    }

    /**
     * Check if account is disabled
     */
    public boolean isDisabled() {
        return status == UserStatus.DISABLED;
    }

    /**
     * Increment failed login attempts
     */
    public void incrementFailedAttempts() {
        this.failedAttempts = (this.failedAttempts == null ? 0 : this.failedAttempts) + 1;
    }

    /**
     * Reset failed attempts on successful login
     */
    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.lockedUntil = null;
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
        }
    }

    /**
     * Lock the account until specified time
     */
    public void lockUntil(LocalDateTime until) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = until;
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
