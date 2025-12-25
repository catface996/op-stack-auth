package com.catface996.auth.domain.repository;

import com.catface996.auth.domain.model.user.User;
import com.catface996.auth.domain.model.user.UserStatus;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for User aggregate
 */
public interface UserRepository {

    /**
     * Find user by ID
     */
    Optional<User> findById(Long id);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email (for login)
     */
    Optional<User> findByUsernameOrEmail(String identifier);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Save user (insert or update)
     */
    User save(User user);

    /**
     * Update failed attempts and lock status
     */
    void updateFailedAttempts(Long userId, int attempts, LocalDateTime lockedUntil);

    /**
     * Update user status
     */
    void updateStatus(Long userId, UserStatus status);
}
