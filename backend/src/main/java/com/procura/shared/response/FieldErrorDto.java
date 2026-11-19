package com.procura.shared.response;

public record FieldErrorDto(
    String field,
    String rejectedValue,
    String message
) {}
