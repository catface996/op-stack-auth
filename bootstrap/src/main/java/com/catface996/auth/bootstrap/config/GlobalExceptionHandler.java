package com.catface996.auth.bootstrap.config;

import com.catface996.auth.common.exception.AuthException;
import com.catface996.auth.common.exception.BusinessException;
import com.catface996.auth.common.result.ErrorCode;
import com.catface996.auth.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理 REST 控制器抛出的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Result<Void>> handleAuthException(AuthException e) {
        log.warn("认证错误: {} - {}", e.getCode(), e.getMessage());
        return ResponseEntity
                .status(mapToHttpStatus(e.getCode()))
                .body(Result.failure(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务错误: {} - {}", e.getCode(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数验证错误: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ErrorCode.INVALID_INPUT, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束违反: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ErrorCode.INVALID_INPUT, message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<Void>> handleBadCredentials(BadCredentialsException e) {
        log.warn("凭证无效: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.failure(ErrorCode.INVALID_CREDENTIALS));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.failure(ErrorCode.AUTHENTICATION_REQUIRED));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDenied(AccessDeniedException e) {
        log.warn("访问拒绝: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Result.failure(ErrorCode.ACCESS_DENIED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGenericException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(ErrorCode.INTERNAL_ERROR));
    }

    private HttpStatus mapToHttpStatus(int code) {
        if (code >= 1001 && code <= 1005) {
            return HttpStatus.UNAUTHORIZED;
        } else if (code == 1005) {
            return HttpStatus.FORBIDDEN;
        } else if (code >= 1006 && code <= 1010) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 2001 && code <= 2099) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 5001 && code <= 5099) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
