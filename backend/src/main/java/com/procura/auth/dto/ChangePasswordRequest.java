package com.procura.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "Current password is required")
    String currentPassword,

    @NotBlank(message = "New password is required")
    @Size(min = 10, message = "Password must be at least 10 characters long (BR-20)")
    String newPassword
) {}
