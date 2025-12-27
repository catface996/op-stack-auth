package com.catface996.auth.application.service;

import com.catface996.auth.application.command.LoginCommand;
import com.catface996.auth.application.command.RegisterCommand;
import com.catface996.auth.application.result.AuthResult;
import com.catface996.auth.application.result.UserInfo;

/**
 * Application service for authentication operations
 */
public interface AuthService {

    /**
     * Register a new user
     * @param command registration details
     * @return user info
     */
    UserInfo register(RegisterCommand command);

    /**
     * Authenticate user and generate token
     * @param command login credentials
     * @return authentication result with token
     */
    AuthResult login(LoginCommand command);

    /**
     * Logout user and invalidate token
     * @param token the token to invalidate
     */
    void logout(String token);

    /**
     * Refresh access token
     * @param refreshToken the refresh token
     * @return new authentication result
     */
    AuthResult refreshToken(String refreshToken);

    /**
     * Get current user info from token
     * @param token the access token
     * @return user info
     */
    UserInfo getCurrentUser(String token);

    /**
     * Validate token and extract user information (for Gateway authentication)
     * @param token the access token to validate
     * @return validation result with user info if valid
     */
    com.catface996.auth.application.result.TokenValidationResult validateToken(String token);
}
