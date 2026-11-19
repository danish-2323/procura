package com.procura.security;

import com.procura.shared.exception.DepartmentScopeException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DepartmentScope {

    private static final Set<String> GLOBAL_ROLES = Set.of(
        "SUPER_ADMIN", "FINANCE_MANAGER", "AUDITOR", "PROCUREMENT_OFFICER"
    );

    public void validateDepartmentAccess(CurrentUser currentUser, Long targetDepartmentId) {
        if (currentUser == null) {
            throw new DepartmentScopeException("Unauthenticated access - department scope cannot be determined");
        }

        if (GLOBAL_ROLES.contains(currentUser.getRole())) {
            return;
        }

        if ("EMPLOYEE".equals(currentUser.getRole()) || "DEPARTMENT_MANAGER".equals(currentUser.getRole())) {
            if (currentUser.getDepartmentId() == null || !currentUser.getDepartmentId().equals(targetDepartmentId)) {
                throw new DepartmentScopeException("User does not have permission to access department " + targetDepartmentId);
            }
        }
    }
}
