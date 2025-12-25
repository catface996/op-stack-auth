package com.catface996.auth.domain.model.user;

/**
 * User account status
 */
public enum UserStatus {
    /**
     * Normal active account
     */
    ACTIVE,

    /**
     * Temporarily locked due to failed login attempts
     */
    LOCKED,

    /**
     * Administratively disabled
     */
    DISABLED
}
