package com.catface996.auth.domain.model.session;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Value object representing JWT token claims
 */
public record TokenClaims(
    Long userId,
    String username,
    Set<String> roles,
    LocalDateTime issuedAt,
    LocalDateTime expiresAt,
    boolean rememberMe,
    String tokenId
) {
    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if user has specific role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
