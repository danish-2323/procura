package com.procura.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Password is required")
    @Size(min = 10, message = "Password must be at least 10 characters long (BR-20)")
    String password,

    @NotNull(message = "Role ID is required")
    Long roleId,

    Long departmentId,
    Long warehouseId
) {}
