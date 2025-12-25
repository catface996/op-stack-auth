package com.catface996.auth.domain.security;

/**
 * Interface for token blacklist operations
 */
public interface TokenBlacklist {

    /**
     * Add a token to the blacklist
     * @param tokenId the token ID (JTI)
     * @param expirationSeconds seconds until the token would have expired
     */
    void addToBlacklist(String tokenId, long expirationSeconds);

    /**
     * Check if a token is blacklisted
     * @param tokenId the token ID (JTI)
     * @return true if the token is blacklisted
     */
    boolean isBlacklisted(String tokenId);

    /**
     * Remove a token from the blacklist (for cleanup)
     * @param tokenId the token ID (JTI)
     */
    void removeFromBlacklist(String tokenId);
}
