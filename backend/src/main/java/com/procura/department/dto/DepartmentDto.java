package com.procura.department.dto;

import com.procura.department.domain.Department;

import java.math.BigDecimal;

public record DepartmentDto(
    Long id,
    String name,
    BigDecimal budgetTotal,
    String currency,
    String fiscalYear,
    Long version
) {
    public static DepartmentDto from(Department d) {
        return new DepartmentDto(
            d.getId(),
            d.getName(),
            d.getBudgetTotal(),
            d.getCurrency(),
            d.getFiscalYear(),
            d.getVersion()
        );
    }
}
