package com.catface996.auth.domain.security;

import com.catface996.auth.domain.model.session.TokenClaims;
import com.catface996.auth.domain.model.user.User;

/**
 * Interface for JWT token operations
 */
public interface TokenProvider {

    /**
     * Generate access token for user
     * @param user the authenticated user
     * @param rememberMe if true, uses extended expiration (30 days)
     * @return JWT token string
     */
    String generateToken(User user, boolean rememberMe);

    /**
     * Parse and validate token
     * @param token JWT token string
     * @return token claims if valid
     * @throws com.catface996.auth.common.exception.AuthException if token is invalid or expired
     */
    TokenClaims parseToken(String token);

    /**
     * Validate token without throwing exception
     * @param token JWT token string
     * @return true if token is valid
     */
    boolean validateToken(String token);

    /**
     * Extract token ID (JTI) from token
     * @param token JWT token string
     * @return token ID
     */
    String getTokenId(String token);

    /**
     * Get token expiration time in seconds
     * @param rememberMe if true, returns extended expiration
     * @return expiration time in seconds
     */
    long getExpirationSeconds(boolean rememberMe);
}
