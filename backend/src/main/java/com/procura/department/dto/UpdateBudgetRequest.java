package com.procura.department.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateBudgetRequest(
    @NotNull(message = "New budget total is required")
    @DecimalMin(value = "0.0", message = "Budget must be non-negative")
    BigDecimal budgetTotal
) {}
