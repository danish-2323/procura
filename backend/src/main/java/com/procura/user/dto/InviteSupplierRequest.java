package com.procura.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InviteSupplierRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Contact name is required")
    String fullName,

    @NotNull(message = "Supplier ID is required")
    Long supplierId,

    @NotBlank(message = "Temporary password is required")
    @Size(min = 10, message = "Password must be at least 10 characters long (BR-20)")
    String temporaryPassword
) {}
