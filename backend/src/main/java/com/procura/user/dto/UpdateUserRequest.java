package com.procura.user.dto;

import com.procura.user.domain.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
    @NotBlank(message = "Full name is required")
    String fullName,

    @NotNull(message = "Role ID is required")
    Long roleId,

    Long departmentId,
    Long warehouseId,
    UserStatus status
) {}
