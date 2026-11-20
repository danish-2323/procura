package com.procura.config;

import com.procura.department.domain.Department;
import com.procura.department.domain.Warehouse;
import com.procura.department.repo.DepartmentRepository;
import com.procura.department.repo.WarehouseRepository;
import com.procura.user.domain.Role;
import com.procura.user.domain.User;
import com.procura.user.domain.UserStatus;
import com.procura.user.repo.RoleRepository;
import com.procura.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@org.springframework.context.annotation.Profile("!prod")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    public static final String DEMO_PASSWORD = "Procura2026!Demo";

    @Override
    @Transactional
    public void run(String... args) {
        ensureRoleExists("SUPER_ADMIN");
        ensureRoleExists("EMPLOYEE");
        ensureRoleExists("DEPARTMENT_MANAGER");
        ensureRoleExists("FINANCE_MANAGER");
        ensureRoleExists("PROCUREMENT_OFFICER");
        ensureRoleExists("WAREHOUSE_MANAGER");
        ensureRoleExists("DIRECTOR");
        ensureRoleExists("AUDITOR");
        ensureRoleExists("SUPPLIER");

        Department engineering = departmentRepository.findByNameAndIsDeletedFalse("Engineering")
            .orElseGet(() -> departmentRepository.save(Department.builder()
                .name("Engineering")
                .budgetTotal(new java.math.BigDecimal("5000000.00"))
                .currency("INR")
                .fiscalYear("2026-2027")
                .build()));

        Department finance = departmentRepository.findByNameAndIsDeletedFalse("Finance")
            .orElseGet(() -> departmentRepository.save(Department.builder()
                .name("Finance")
                .budgetTotal(new java.math.BigDecimal("2500000.00"))
                .currency("INR")
                .fiscalYear("2026-2027")
                .build()));

        Department procurement = departmentRepository.findByNameAndIsDeletedFalse("Procurement")
            .orElseGet(() -> departmentRepository.save(Department.builder()
                .name("Procurement")
                .budgetTotal(new java.math.BigDecimal("1800000.00"))
                .currency("INR")
                .fiscalYear("2026-2027")
                .build()));

        Warehouse mumbaiWh = warehouseRepository.findByCode("WH-MUM-01")
            .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                .code("WH-MUM-01")
                .name("Mumbai Central Logistics Hub")
                .address("Plot 42, MIDC, Mumbai")
                .build()));

        seedUser("admin@procura.local", "System Administrator", "SUPER_ADMIN", null, null);
        seedUser("employee@procura.local", "Aarav Sharma (Engineer)", "EMPLOYEE", engineering, null);
        seedUser("manager.eng@procura.local", "Priya Nair (Engineering Head)", "DEPARTMENT_MANAGER", engineering, null);
        seedUser("finance@procura.local", "Vikram Mehta (Finance Controller)", "FINANCE_MANAGER", finance, null);
        seedUser("procurement@procura.local", "Neha Verma (Lead Procurement)", "PROCUREMENT_OFFICER", procurement, null);
        seedUser("warehouse@procura.local", "Rajesh Patil (Warehouse Lead)", "WAREHOUSE_MANAGER", null, mumbaiWh);
        seedUser("director@procura.local", "Anita Roy (Executive Director)", "DIRECTOR", null, null);
        seedUser("auditor@procura.local", "Kavita Rao (Internal Auditor)", "AUDITOR", null, null);
        seedUser("supplier.dell@procura.local", "Dell Enterprise India Representative", "SUPPLIER", null, null);

        log.info("Phase 1 Data Initialization completed: Seeded users for all 9 roles.");
    }

    private Role ensureRoleExists(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    private void seedUser(String email, String fullName, String roleName, Department department, Warehouse warehouse) {
        if (!userRepository.existsByEmailAndIsDeletedFalse(email)) {
            Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " missing"));

            User user = User.builder()
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordEncoder.encode(DEMO_PASSWORD))
                .role(role)
                .department(department)
                .warehouse(warehouse)
                .status(UserStatus.ACTIVE)
                .failedAttempts(0)
                .isDeleted(false)
                .build();

            userRepository.save(user);
        }
    }
}
