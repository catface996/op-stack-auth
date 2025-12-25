package com.catface996.auth.infrastructure.security.jwt;

import com.catface996.auth.common.exception.AuthException;
import com.catface996.auth.domain.model.session.TokenClaims;
import com.catface996.auth.domain.model.user.User;
import com.catface996.auth.domain.security.TokenProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * JWT implementation of TokenProvider
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_REMEMBER_ME = "rememberMe";

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user, boolean rememberMe) {
        Instant now = Instant.now();
        long expirationSeconds = getExpirationSeconds(rememberMe);
        Instant expiration = now.plusSeconds(expirationSeconds);
        String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .id(tokenId)
                .issuer(jwtProperties.getIssuer())
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_USERNAME, user.getUsername())
                .claim(CLAIM_ROLES, user.getRoles())
                .claim(CLAIM_REMEMBER_ME, rememberMe)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public TokenClaims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return toTokenClaims(claims);
        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            throw AuthException.tokenExpired();
        } catch (SecurityException | MalformedJwtException e) {
            log.debug("Invalid token signature: {}", e.getMessage());
            throw AuthException.invalidToken();
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported token: {}", e.getMessage());
            throw AuthException.invalidToken();
        } catch (IllegalArgumentException e) {
            log.debug("Token claims string is empty: {}", e.getMessage());
            throw AuthException.invalidToken();
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getTokenId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getId();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to extract token ID: {}", e.getMessage());
            throw AuthException.invalidToken();
        }
    }

    @Override
    public long getExpirationSeconds(boolean rememberMe) {
        return rememberMe ? jwtProperties.getRememberMeExpirationSeconds()
                         : jwtProperties.getExpirationSeconds();
    }

    private TokenClaims toTokenClaims(Claims claims) {
        Long userId = claims.get(CLAIM_USER_ID, Long.class);
        String username = claims.get(CLAIM_USERNAME, String.class);
        Boolean rememberMe = claims.get(CLAIM_REMEMBER_ME, Boolean.class);

        @SuppressWarnings("unchecked")
        List<String> rolesList = claims.get(CLAIM_ROLES, List.class);
        Set<String> roles = rolesList != null ? new HashSet<>(rolesList) : Set.of();

        LocalDateTime issuedAt = claims.getIssuedAt() != null
                ? LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault())
                : null;
        LocalDateTime expiresAt = claims.getExpiration() != null
                ? LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault())
                : null;

        return new TokenClaims(
                userId,
                username,
                roles,
                issuedAt,
                expiresAt,
                rememberMe != null && rememberMe,
                claims.getId()
        );
    }
}
