package com.procura.security;

import com.procura.shared.exception.DepartmentScopeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepartmentScopeTest {

    private final DepartmentScope departmentScope = new DepartmentScope();

    @Test
    @DisplayName("BR-19: EMPLOYEE can only access their own department records")
    void br19_departmentScopeIsolation() {
        CurrentUser employeeDept1 = CurrentUser.builder()
            .id(1L)
            .email("emp1@procura.local")
            .role("EMPLOYEE")
            .departmentId(10L)
            .active(true)
            .build();

        // Accessing own department (10L) succeeds
        assertThatCode(() -> departmentScope.validateDepartmentAccess(employeeDept1, 10L))
            .doesNotThrowAnyException();

        // Accessing another department (20L) must throw DepartmentScopeException
        assertThatThrownBy(() -> departmentScope.validateDepartmentAccess(employeeDept1, 20L))
            .isInstanceOf(DepartmentScopeException.class)
            .hasMessageContaining("User does not have permission to access department 20");
    }

    @Test
    @DisplayName("BR-19: FINANCE_MANAGER and SUPER_ADMIN have global departmental visibility")
    void br19_globalRoleScope() {
        CurrentUser financeMgr = CurrentUser.builder()
            .id(2L)
            .email("fin@procura.local")
            .role("FINANCE_MANAGER")
            .departmentId(5L)
            .active(true)
            .build();

        CurrentUser superAdmin = CurrentUser.builder()
            .id(3L)
            .email("admin@procura.local")
            .role("SUPER_ADMIN")
            .departmentId(null)
            .active(true)
            .build();

        // Any department access succeeds
        assertThatCode(() -> departmentScope.validateDepartmentAccess(financeMgr, 99L))
            .doesNotThrowAnyException();

        assertThatCode(() -> departmentScope.validateDepartmentAccess(superAdmin, 99L))
            .doesNotThrowAnyException();
    }
}
