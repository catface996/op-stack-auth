package com.catface996.auth.interfaces.http.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard success response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> of(T data) {
        return SuccessResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> SuccessResponse<T> of(T data, String message) {
        return SuccessResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static SuccessResponse<Void> ok() {
        return SuccessResponse.<Void>builder()
                .success(true)
                .build();
    }

    public static SuccessResponse<Void> ok(String message) {
        return SuccessResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
    }
}
