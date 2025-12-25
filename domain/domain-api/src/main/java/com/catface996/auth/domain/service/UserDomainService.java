package com.catface996.auth.domain.service;

import com.catface996.auth.domain.model.user.User;

/**
 * Domain service for user-related operations
 */
public interface UserDomainService {

    /**
     * Validate user credentials
     * @param identifier username or email
     * @param password raw password
     * @return authenticated user
     */
    User authenticate(String identifier, String password);

    /**
     * Check if user account is locked
     * @param user the user to check
     * @return true if account is locked
     */
    boolean isAccountLocked(User user);

    /**
     * Handle failed login attempt
     * @param user the user who failed to login
     */
    void handleFailedLoginAttempt(User user);

    /**
     * Reset failed login attempts after successful login
     * @param user the user who logged in successfully
     */
    void resetFailedAttempts(User user);
}
