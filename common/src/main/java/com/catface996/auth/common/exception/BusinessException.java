package com.catface996.auth.common.exception;

import com.catface996.auth.common.result.ErrorCode;
import lombok.Getter;

/**
 * Base exception for business logic errors
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final String errorMessage;
    private final ErrorCode errorCode;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.errorMessage = message;
        this.errorCode = null;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.errorMessage = message;
        this.errorCode = errorCode;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.errorMessage = message;
        this.errorCode = null;
    }
}
