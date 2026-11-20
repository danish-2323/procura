package com.procura.department.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateDepartmentRequest(
    @NotBlank(message = "Department name is required")
    String name,

    @NotNull(message = "Budget total is required")
    @DecimalMin(value = "0.0", message = "Budget must be non-negative")
    BigDecimal budgetTotal,

    String currency,

    @NotBlank(message = "Fiscal year is required")
    String fiscalYear
) {}
