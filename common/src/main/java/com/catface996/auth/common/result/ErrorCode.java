package com.catface996.auth.common.result;

import lombok.Getter;

/**
 * 错误码枚举
 * 错误码规范：
 * - 0: 成功
 * - 1001-1099: 认证错误
 * - 2001-2099: 参数验证错误
 * - 5001-5099: 系统错误
 */
@Getter
public enum ErrorCode {

    // 成功
    SUCCESS(0, "操作成功"),

    // 认证错误 (1001-1099)
    INVALID_CREDENTIALS(1001, "用户名或密码错误"),
    ACCOUNT_LOCKED(1002, "账号已被锁定"),
    TOKEN_EXPIRED(1003, "令牌已过期"),
    TOKEN_INVALID(1004, "令牌无效"),
    ACCESS_DENIED(1005, "访问被拒绝"),
    EMAIL_EXISTS(1006, "邮箱已被注册"),
    USERNAME_EXISTS(1007, "用户名已被注册"),
    PASSWORD_POLICY_VIOLATION(1008, "密码不符合安全策略"),
    ACCOUNT_DISABLED(1009, "账号已被禁用"),
    AUTHENTICATION_REQUIRED(1010, "需要身份认证"),

    // 参数验证错误 (2001-2099)
    INVALID_INPUT(2001, "输入参数无效"),
    MISSING_REQUIRED_FIELD(2002, "缺少必填字段"),
    INVALID_EMAIL_FORMAT(2003, "邮箱格式不正确"),
    INVALID_USERNAME_FORMAT(2004, "用户名格式不正确"),

    // 系统错误 (5001-5099)
    INTERNAL_ERROR(5001, "系统内部错误"),
    SERVICE_UNAVAILABLE(5002, "服务暂时不可用"),
    DATABASE_ERROR(5003, "数据库错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
