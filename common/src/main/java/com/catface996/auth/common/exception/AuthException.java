package com.catface996.auth.common.exception;

import com.catface996.auth.common.result.ErrorCode;

/**
 * Exception for authentication and authorization errors
 */
public class AuthException extends BusinessException {

    public AuthException(String code, String message) {
        super(code, message);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static AuthException invalidCredentials() {
        return new AuthException(ErrorCode.AUTH_001);
    }

    public static AuthException accountLocked() {
        return new AuthException(ErrorCode.AUTH_002);
    }

    public static AuthException tokenExpired() {
        return new AuthException(ErrorCode.AUTH_003);
    }

    public static AuthException tokenInvalid() {
        return new AuthException(ErrorCode.AUTH_004);
    }

    public static AuthException invalidToken() {
        return new AuthException(ErrorCode.AUTH_004);
    }

    public static AuthException accessDenied() {
        return new AuthException(ErrorCode.AUTH_005);
    }

    public static AuthException accountInactive() {
        return new AuthException(ErrorCode.AUTH_009);
    }

    public static AuthException emailExists() {
        return new AuthException(ErrorCode.AUTH_006);
    }

    public static AuthException usernameExists() {
        return new AuthException(ErrorCode.AUTH_007);
    }

    public static AuthException passwordPolicyViolation(String message) {
        return new AuthException(ErrorCode.AUTH_008, message);
    }

    public static AuthException accountDisabled() {
        return new AuthException(ErrorCode.AUTH_009);
    }

    public static AuthException authenticationRequired() {
        return new AuthException(ErrorCode.AUTH_010);
    }
}
