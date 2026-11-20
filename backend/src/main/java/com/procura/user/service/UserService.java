package com.procura.user.service;

import com.procura.department.domain.Department;
import com.procura.department.domain.Warehouse;
import com.procura.department.repo.DepartmentRepository;
import com.procura.department.repo.WarehouseRepository;
import com.procura.shared.exception.BusinessRuleException;
import com.procura.shared.exception.ResourceNotFoundException;
import com.procura.shared.pagination.PagedResponse;
import com.procura.shared.pagination.PaginationParams;
import com.procura.user.domain.Role;
import com.procura.user.domain.User;
import com.procura.user.domain.UserStatus;
import com.procura.user.dto.CreateUserRequest;
import com.procura.user.dto.InviteSupplierRequest;
import com.procura.user.dto.UpdateUserRequest;
import com.procura.user.dto.UserDto;
import com.procura.user.repo.RoleRepository;
import com.procura.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(CreateUserRequest req) {
        if (req.password().length() < 10) {
            throw new BusinessRuleException("BR-20", "Password must be at least 10 characters long");
        }

        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }

        Role role = roleRepository.findById(req.roleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role", req.roleId()));

        Department department = null;
        if (req.departmentId() != null) {
            department = departmentRepository.findById(req.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", req.departmentId()));
        }

        Warehouse warehouse = null;
        if (req.warehouseId() != null) {
            warehouse = warehouseRepository.findById(req.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", req.warehouseId()));
        }

        User user = User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(req.password()))
            .fullName(req.fullName())
            .role(role)
            .department(department)
            .warehouse(warehouse)
            .status(UserStatus.ACTIVE)
            .failedAttempts(0)
            .isDeleted(false)
            .build();

        User saved = userRepository.save(user);
        return UserDto.from(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        return userRepository.findById(id)
            .filter(u -> !u.getIsDeleted())
            .map(UserDto::from)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserDto> listUsers(PaginationParams params) {
        Page<User> page = userRepository.findByIsDeletedFalse(params.toPageable());
        return PagedResponse.from(page.map(UserDto::from));
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
            .filter(u -> !u.getIsDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        Role role = roleRepository.findById(req.roleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role", req.roleId()));

        Department department = null;
        if (req.departmentId() != null) {
            department = departmentRepository.findById(req.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", req.departmentId()));
        }

        Warehouse warehouse = null;
        if (req.warehouseId() != null) {
            warehouse = warehouseRepository.findById(req.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", req.warehouseId()));
        }

        user.setFullName(req.fullName());
        user.setRole(role);
        user.setDepartment(department);
        user.setWarehouse(warehouse);
        if (req.status() != null) {
            user.setStatus(req.status());
        }

        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public UserDto unlockUser(Long id) {
        User user = userRepository.findById(id)
            .filter(u -> !u.getIsDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.resetFailedLogins();
        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        // Soft delete only per BR-18
        User user = userRepository.findById(id)
            .filter(u -> !u.getIsDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setIsDeleted(true);
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
    }

    @Transactional
    public UserDto inviteSupplierUser(InviteSupplierRequest req) {
        if (req.temporaryPassword().length() < 10) {
            throw new BusinessRuleException("BR-20", "Password must be at least 10 characters long");
        }

        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }

        Role supplierRole = roleRepository.findByName("SUPPLIER")
            .orElseThrow(() -> new ResourceNotFoundException("Role SUPPLIER not found"));

        User user = User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(req.temporaryPassword()))
            .fullName(req.fullName())
            .role(supplierRole)
            .status(UserStatus.ACTIVE)
            .failedAttempts(0)
            .isDeleted(false)
            .build();

        User saved = userRepository.save(user);
        return UserDto.from(saved);
    }
}
