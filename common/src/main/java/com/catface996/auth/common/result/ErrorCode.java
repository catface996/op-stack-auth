package com.catface996.auth.common.result;

import lombok.Getter;

/**
 * Error codes for authentication service
 */
@Getter
public enum ErrorCode {

    // Authentication errors (AUTH_0XX)
    AUTH_001("AUTH_001", "Invalid credentials"),
    AUTH_002("AUTH_002", "Account is locked"),
    AUTH_003("AUTH_003", "Token expired"),
    AUTH_004("AUTH_004", "Token invalid"),
    AUTH_005("AUTH_005", "Access denied"),
    AUTH_006("AUTH_006", "Email already exists"),
    AUTH_007("AUTH_007", "Username already exists"),
    AUTH_008("AUTH_008", "Password policy violation"),
    AUTH_009("AUTH_009", "Account disabled"),
    AUTH_010("AUTH_010", "Authentication required"),

    // Validation errors (VAL_0XX)
    VAL_001("VAL_001", "Invalid input"),
    VAL_002("VAL_002", "Missing required field"),
    VAL_003("VAL_003", "Invalid email format"),
    VAL_004("VAL_004", "Invalid username format"),

    // System errors (SYS_0XX)
    SYS_001("SYS_001", "Internal server error"),
    SYS_002("SYS_002", "Service unavailable"),
    SYS_003("SYS_003", "Database error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
