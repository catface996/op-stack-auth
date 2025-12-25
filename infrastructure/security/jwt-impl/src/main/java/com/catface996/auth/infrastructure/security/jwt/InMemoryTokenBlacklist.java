package com.catface996.auth.infrastructure.security.jwt;

import com.catface996.auth.domain.security.TokenBlacklist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of TokenBlacklist.
 * Suitable for single-instance deployments.
 * For distributed deployments, use a Redis-based implementation.
 */
@Slf4j
@Component
public class InMemoryTokenBlacklist implements TokenBlacklist {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    @Override
    public void addToBlacklist(String tokenId, long expirationSeconds) {
        Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);
        blacklistedTokens.put(tokenId, expiresAt);
        log.debug("Token {} added to blacklist, expires at {}", tokenId, expiresAt);
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        Instant expiresAt = blacklistedTokens.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (Instant.now().isAfter(expiresAt)) {
            blacklistedTokens.remove(tokenId);
            return false;
        }
        return true;
    }

    @Override
    public void removeFromBlacklist(String tokenId) {
        blacklistedTokens.remove(tokenId);
        log.debug("Token {} removed from blacklist", tokenId);
    }

    /**
     * Cleanup expired tokens every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int removed = 0;
        var iterator = blacklistedTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (now.isAfter(entry.getValue())) {
                iterator.remove();
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("Cleaned up {} expired tokens from blacklist", removed);
        }
    }
}
