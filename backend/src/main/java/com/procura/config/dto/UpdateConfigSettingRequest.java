package com.procura.config.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateConfigSettingRequest(
    @NotBlank(message = "Setting key is required")
    String key,

    @NotBlank(message = "Setting value is required")
    String value
) {}
