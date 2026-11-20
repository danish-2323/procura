package com.procura.user.dto;

import com.procura.user.domain.User;
import com.procura.user.domain.UserStatus;

public record UserDto(
    Long id,
    String email,
    String fullName,
    String role,
    Long departmentId,
    String departmentName,
    Long warehouseId,
    String warehouseCode,
    UserStatus status,
    Integer failedAttempts,
    Boolean isDeleted
) {
    public static UserDto from(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().getName(),
            user.getDepartment() != null ? user.getDepartment().getId() : null,
            user.getDepartment() != null ? user.getDepartment().getName() : null,
            user.getWarehouse() != null ? user.getWarehouse().getId() : null,
            user.getWarehouse() != null ? user.getWarehouse().getCode() : null,
            user.getStatus(),
            user.getFailedAttempts(),
            user.getIsDeleted()
        );
    }
}
