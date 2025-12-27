package com.catface996.auth.common.exception;

import com.catface996.auth.common.result.ErrorCode;

/**
 * 认证授权异常
 * 处理身份认证和权限相关的错误
 */
public class AuthException extends BusinessException {

    public AuthException(int code, String message) {
        super(code, message);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static AuthException invalidCredentials() {
        return new AuthException(ErrorCode.INVALID_CREDENTIALS);
    }

    public static AuthException accountLocked() {
        return new AuthException(ErrorCode.ACCOUNT_LOCKED);
    }

    public static AuthException tokenExpired() {
        return new AuthException(ErrorCode.TOKEN_EXPIRED);
    }

    public static AuthException tokenInvalid() {
        return new AuthException(ErrorCode.TOKEN_INVALID);
    }

    public static AuthException invalidToken() {
        return new AuthException(ErrorCode.TOKEN_INVALID);
    }

    public static AuthException accessDenied() {
        return new AuthException(ErrorCode.ACCESS_DENIED);
    }

    public static AuthException accountInactive() {
        return new AuthException(ErrorCode.ACCOUNT_DISABLED);
    }

    public static AuthException emailExists() {
        return new AuthException(ErrorCode.EMAIL_EXISTS);
    }

    public static AuthException usernameExists() {
        return new AuthException(ErrorCode.USERNAME_EXISTS);
    }

    public static AuthException passwordPolicyViolation(String message) {
        return new AuthException(ErrorCode.PASSWORD_POLICY_VIOLATION, message);
    }

    public static AuthException accountDisabled() {
        return new AuthException(ErrorCode.ACCOUNT_DISABLED);
    }

    public static AuthException authenticationRequired() {
        return new AuthException(ErrorCode.AUTHENTICATION_REQUIRED);
    }
}
