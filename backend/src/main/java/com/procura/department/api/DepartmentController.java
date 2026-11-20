package com.procura.department.api;

import com.procura.department.dto.CreateDepartmentRequest;
import com.procura.department.dto.DepartmentDto;
import com.procura.department.dto.UpdateBudgetRequest;
import com.procura.department.service.DepartmentService;
import com.procura.shared.pagination.PagedResponse;
import com.procura.shared.pagination.PaginationParams;
import com.procura.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department & Org Management", description = "Endpoints for managing departments and fiscal budgets")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "List all active departments (for dropdowns and filters)")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> list = departmentService.listAllDepartments();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/paged")
    @Operation(summary = "List departments with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentDto>>> listPagedDepartments(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PaginationParams params = new PaginationParams(page, size, sortBy, sortDir);
        PagedResponse<DepartmentDto> response = departmentService.listDepartments(params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartment(@PathVariable Long id) {
        DepartmentDto department = departmentService.getDepartment(id);
        return ResponseEntity.ok(ApiResponse.ok(department));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create department (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDto created = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(created));
    }

    @PutMapping("/{id}/budget")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update department total allocated budget (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateBudget(
        @PathVariable Long id,
        @Valid @RequestBody UpdateBudgetRequest request
    ) {
        DepartmentDto updated = departmentService.updateBudget(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Department budget updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Soft delete department (SUPER_ADMIN only, BR-18)")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Department deleted"));
    }
}
