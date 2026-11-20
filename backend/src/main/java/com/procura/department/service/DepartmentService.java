package com.procura.department.service;

import com.procura.department.domain.Department;
import com.procura.department.dto.CreateDepartmentRequest;
import com.procura.department.dto.DepartmentDto;
import com.procura.department.dto.UpdateBudgetRequest;
import com.procura.department.repo.DepartmentRepository;
import com.procura.shared.exception.ResourceNotFoundException;
import com.procura.shared.money.MoneyUtils;
import com.procura.shared.pagination.PagedResponse;
import com.procura.shared.pagination.PaginationParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public DepartmentDto createDepartment(CreateDepartmentRequest req) {
        if (departmentRepository.existsByNameAndIsDeletedFalse(req.name().trim())) {
            throw new IllegalArgumentException("Department with name '" + req.name() + "' already exists");
        }

        Department department = Department.builder()
            .name(req.name().trim())
            .budgetTotal(MoneyUtils.round(req.budgetTotal()))
            .currency(req.currency() != null && !req.currency().isBlank() ? req.currency().toUpperCase() : MoneyUtils.DEFAULT_CURRENCY)
            .fiscalYear(req.fiscalYear().trim())
            .isDeleted(false)
            .build();

        return DepartmentDto.from(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartment(Long id) {
        return departmentRepository.findById(id)
            .filter(d -> !d.getIsDeleted())
            .map(DepartmentDto::from)
            .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<DepartmentDto> listDepartments(PaginationParams params) {
        Page<Department> page = departmentRepository.findByIsDeletedFalse(params.toPageable());
        return PagedResponse.from(page.map(DepartmentDto::from));
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> listAllDepartments() {
        return departmentRepository.findAllByIsDeletedFalse().stream()
            .map(DepartmentDto::from)
            .toList();
    }

    @Transactional
    public DepartmentDto updateBudget(Long id, UpdateBudgetRequest req) {
        Department department = departmentRepository.findById(id)
            .filter(d -> !d.getIsDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("Department", id));

        department.setBudgetTotal(MoneyUtils.round(req.budgetTotal()));
        return DepartmentDto.from(departmentRepository.save(department));
    }

    @Transactional
    public void deleteDepartment(Long id) {
        // Soft delete master data per BR-18
        Department department = departmentRepository.findById(id)
            .filter(d -> !d.getIsDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("Department", id));

        department.setIsDeleted(true);
        departmentRepository.save(department);
    }
}
