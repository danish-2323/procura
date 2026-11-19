package com.procura.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.procura.shared.exception.ApiErrorCode;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    Instant timestamp,
    int status,
    ApiErrorCode code,
    String message,
    String path,
    String traceId,
    String ruleId,
    List<FieldErrorDto> fieldErrors
) {
    public static ErrorResponse of(int status, ApiErrorCode code, String message, String path, String traceId) {
        return new ErrorResponse(Instant.now(), status, code, message, path, traceId, null, List.of());
    }

    public static ErrorResponse withRule(int status, ApiErrorCode code, String message, String ruleId, String path, String traceId) {
        return new ErrorResponse(Instant.now(), status, code, message, path, traceId, ruleId, List.of());
    }

    public static ErrorResponse validation(int status, ApiErrorCode code, String message, String path, String traceId, List<FieldErrorDto> fieldErrors) {
        return new ErrorResponse(Instant.now(), status, code, message, path, traceId, null, fieldErrors);
    }
}
