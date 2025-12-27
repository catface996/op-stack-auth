package com.catface996.auth.application.service.impl;

import com.catface996.auth.application.command.LoginCommand;
import com.catface996.auth.application.command.RegisterCommand;
import com.catface996.auth.application.result.AuthResult;
import com.catface996.auth.application.result.TokenValidationResult;
import com.catface996.auth.application.result.UserInfo;
import com.catface996.auth.application.service.AuthService;
import com.catface996.auth.common.exception.AuthException;
import com.catface996.auth.domain.model.session.TokenClaims;
import com.catface996.auth.domain.model.user.LoginAttempt;
import com.catface996.auth.domain.model.user.User;
import com.catface996.auth.domain.model.user.UserStatus;
import com.catface996.auth.domain.repository.LoginAttemptRepository;
import com.catface996.auth.domain.repository.RoleRepository;
import com.catface996.auth.domain.repository.UserRepository;
import com.catface996.auth.domain.security.TokenBlacklist;
import com.catface996.auth.domain.security.TokenProvider;
import com.catface996.auth.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Implementation of AuthService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final UserDomainService userDomainService;
    private final TokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserInfo register(RegisterCommand command) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(command.getUsername())) {
            throw AuthException.usernameExists();
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(command.getEmail())) {
            throw AuthException.emailExists();
        }

        // Create user
        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .passwordHash(passwordEncoder.encode(command.getPassword()))
                .status(UserStatus.ACTIVE)
                .failedAttempts(0)
                .roles(Set.of(DEFAULT_ROLE))
                .build();

        User savedUser = userRepository.save(user);

        // Assign default role
        roleRepository.findByName(DEFAULT_ROLE)
                .ifPresent(role -> roleRepository.assignRole(savedUser.getId(), role.getId()));

        log.info("User registered successfully: {}", savedUser.getUsername());

        return toUserInfo(savedUser);
    }

    @Override
    @Transactional
    public AuthResult login(LoginCommand command) {
        User user = userDomainService.authenticate(command.getIdentifier(), command.getPassword());

        // Record login attempt
        LoginAttempt attempt = LoginAttempt.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .success(true)
                .ipAddress(command.getIpAddress())
                .userAgent(command.getUserAgent())
                .build();
        loginAttemptRepository.save(attempt);

        // Generate token
        String token = tokenProvider.generateToken(user, command.isRememberMe());
        long expiresIn = tokenProvider.getExpirationSeconds(command.isRememberMe());

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResult.of(token, expiresIn, toUserInfo(user));
    }

    @Override
    public void logout(String token) {
        if (token != null && tokenProvider.validateToken(token)) {
            String tokenId = tokenProvider.getTokenId(token);
            TokenClaims claims = tokenProvider.parseToken(token);
            long remainingSeconds = java.time.Duration.between(
                    java.time.LocalDateTime.now(), claims.expiresAt()).getSeconds();
            if (remainingSeconds > 0) {
                tokenBlacklist.addToBlacklist(tokenId, remainingSeconds);
            }
            log.info("User logged out, token {} blacklisted", tokenId);
        }
    }

    @Override
    public AuthResult refreshToken(String refreshToken) {
        TokenClaims claims = tokenProvider.parseToken(refreshToken);

        User user = userRepository.findById(claims.userId())
                .orElseThrow(AuthException::invalidToken);

        if (!user.isActive()) {
            throw AuthException.accountInactive();
        }

        String newToken = tokenProvider.generateToken(user, claims.rememberMe());
        long expiresIn = tokenProvider.getExpirationSeconds(claims.rememberMe());

        return AuthResult.of(newToken, expiresIn, toUserInfo(user));
    }

    @Override
    public UserInfo getCurrentUser(String token) {
        TokenClaims claims = tokenProvider.parseToken(token);

        User user = userRepository.findById(claims.userId())
                .orElseThrow(AuthException::invalidToken);

        return toUserInfo(user);
    }

    @Override
    public TokenValidationResult validateToken(String token) {
        try {
            // Check if token is valid
            if (!tokenProvider.validateToken(token)) {
                return TokenValidationResult.failure("Invalid token");
            }

            // Check if token is blacklisted
            String tokenId = tokenProvider.getTokenId(token);
            if (tokenBlacklist.isBlacklisted(tokenId)) {
                return TokenValidationResult.failure("Token has been revoked");
            }

            // Parse token claims
            TokenClaims claims = tokenProvider.parseToken(token);

            // Check if token is expired
            if (claims.isExpired()) {
                return TokenValidationResult.failure("Token has expired");
            }

            log.debug("Token validated successfully for user: {}", claims.username());

            return TokenValidationResult.success(
                    claims.userId(),
                    claims.username(),
                    claims.roles()
            );
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return TokenValidationResult.failure(e.getMessage());
        }
    }

    private UserInfo toUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .roles(user.getRoles())
                .build();
    }
}
