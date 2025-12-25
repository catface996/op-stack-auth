package com.catface996.auth.infrastructure.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens (min 256 bits for HS256)
     */
    private String secret = "your-256-bit-secret-key-for-jwt-signing-please-change-in-production";

    /**
     * Token expiration time in seconds (default: 1 hour)
     */
    private long expirationSeconds = 3600;

    /**
     * Remember-me token expiration time in seconds (default: 30 days)
     */
    private long rememberMeExpirationSeconds = 2592000;

    /**
     * Token issuer
     */
    private String issuer = "op-stack-auth";
}
