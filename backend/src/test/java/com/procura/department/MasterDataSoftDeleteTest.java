package com.procura.department;

import com.procura.department.domain.Department;
import com.procura.department.repo.DepartmentRepository;
import com.procura.department.service.DepartmentService;
import com.procura.user.domain.Role;
import com.procura.user.domain.User;
import com.procura.user.repo.RoleRepository;
import com.procura.user.repo.UserRepository;
import com.procura.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MasterDataSoftDeleteTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("BR-18: Master data records (departments, users) are soft-deleted, not removed from DB")
    void br18_noHardDeleteFinancials() {
        // 1. Department soft-delete
        Department dept = departmentRepository.save(Department.builder()
            .name("Temporary Master Dept")
            .budgetTotal(BigDecimal.valueOf(100000))
            .currency("INR")
            .fiscalYear("2026-2027")
            .isDeleted(false)
            .build());

        departmentService.deleteDepartment(dept.getId());

        // Still exists in DB table
        Department inDb = departmentRepository.findById(dept.getId()).orElseThrow();
        assertThat(inDb.getIsDeleted()).isTrue();

        // But filtered out of active query
        assertThat(departmentRepository.findByNameAndIsDeletedFalse("Temporary Master Dept")).isEmpty();

        // 2. User soft-delete
        Role role = roleRepository.findByName("EMPLOYEE").orElseThrow();
        User user = userRepository.save(User.builder()
            .email("softdelete.user@procura.local")
            .fullName("Soft Delete User")
            .passwordHash("DummyHashForTest123")
            .role(role)
            .isDeleted(false)
            .build());

        userService.deleteUser(user.getId());

        User userInDb = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userInDb.getIsDeleted()).isTrue();
        assertThat(userRepository.findByEmailAndIsDeletedFalse("softdelete.user@procura.local")).isEmpty();
    }
}
