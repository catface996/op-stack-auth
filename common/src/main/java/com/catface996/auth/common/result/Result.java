package com.catface996.auth.common.result;

import lombok.Data;

/**
 * Generic API response wrapper
 */
@Data
public class Result<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;
    private String traceId;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode("SUCCESS");
        result.setMessage("Operation completed successfully");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> failure(String code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> failure(ErrorCode errorCode) {
        return failure(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> Result<T> failure(ErrorCode errorCode, String message) {
        return failure(errorCode.getCode(), message);
    }

    public Result<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
