package com.catface996.auth.domain.service.impl;

import com.catface996.auth.common.exception.AuthException;
import com.catface996.auth.domain.model.user.User;
import com.catface996.auth.domain.model.user.UserStatus;
import com.catface996.auth.domain.repository.UserRepository;
import com.catface996.auth.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of UserDomainService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${auth.security.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    @Override
    public User authenticate(String identifier, String password) {
        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(AuthException::invalidCredentials);

        if (!user.isActive()) {
            throw AuthException.accountInactive();
        }

        if (isAccountLocked(user)) {
            throw AuthException.accountLocked();
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            handleFailedLoginAttempt(user);
            throw AuthException.invalidCredentials();
        }

        resetFailedAttempts(user);
        return user;
    }

    @Override
    public boolean isAccountLocked(User user) {
        return user.isLocked();
    }

    @Override
    public void handleFailedLoginAttempt(User user) {
        int newAttempts = user.getFailedAttempts() + 1;
        LocalDateTime lockedUntil = null;

        if (newAttempts >= maxFailedAttempts) {
            lockedUntil = LocalDateTime.now().plusMinutes(lockoutDurationMinutes);
            log.warn("Account locked for user {} until {}", user.getUsername(), lockedUntil);
        }

        userRepository.updateFailedAttempts(user.getId(), newAttempts, lockedUntil);
    }

    @Override
    public void resetFailedAttempts(User user) {
        if (user.getFailedAttempts() > 0) {
            userRepository.updateFailedAttempts(user.getId(), 0, null);
        }
    }
}
