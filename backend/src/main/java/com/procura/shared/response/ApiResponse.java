package com.procura.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    Instant timestamp,
    int status,
    T data,
    String message
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(Instant.now(), 200, data, "SUCCESS");
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(Instant.now(), 200, data, message);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(Instant.now(), 201, data, "CREATED");
    }
}
