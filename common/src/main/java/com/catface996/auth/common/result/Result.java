package com.catface996.auth.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一响应结果
 * 所有 API 接口的返回值都应使用此类包装
 */
@Data
@Schema(description = "统一响应结果")
public class Result<T> {

    @Schema(description = "是否成功", example = "true")
    private boolean success;

    @Schema(description = "响应码，0表示成功，非0表示失败", example = "0")
    private int code;

    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "追踪ID（网关注入）", example = "trace-123456")
    private String traceId;

    private Result() {}

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ErrorCode.SUCCESS.getCode());
        result.setMessage(ErrorCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> failure(int code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（使用错误码枚举）
     */
    public static <T> Result<T> failure(ErrorCode errorCode) {
        return failure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 失败响应（使用错误码枚举，自定义消息）
     */
    public static <T> Result<T> failure(ErrorCode errorCode, String message) {
        return failure(errorCode.getCode(), message);
    }

    /**
     * 设置追踪ID
     */
    public Result<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
