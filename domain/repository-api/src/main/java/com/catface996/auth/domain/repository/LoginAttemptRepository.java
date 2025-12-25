package com.catface996.auth.domain.repository;

import com.catface996.auth.domain.model.user.LoginAttempt;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for LoginAttempt entity
 */
public interface LoginAttemptRepository {

    /**
     * Save login attempt
     */
    void save(LoginAttempt attempt);

    /**
     * Find recent login attempts for user
     */
    List<LoginAttempt> findByUserId(Long userId, int limit);

    /**
     * Count failed attempts since a given time
     */
    int countRecentFailedAttempts(Long userId, LocalDateTime since);

    /**
     * Find login attempts by username (for audit)
     */
    List<LoginAttempt> findByUsername(String username, int limit);
}
