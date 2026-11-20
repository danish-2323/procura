package com.procura.user.api;

import com.procura.shared.pagination.PagedResponse;
import com.procura.shared.pagination.PaginationParams;
import com.procura.shared.response.ApiResponse;
import com.procura.user.dto.CreateUserRequest;
import com.procura.user.dto.InviteSupplierRequest;
import com.procura.user.dto.UpdateUserRequest;
import com.procura.user.dto.UserDto;
import com.procura.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Administration of users and roles (SUPER_ADMIN only)")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create new employee/manager/director/auditor user")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(created));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List users with pagination and filtering")
    public ResponseEntity<ApiResponse<PagedResponse<UserDto>>> listUsers(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PaginationParams params = new PaginationParams(page, size, sortBy, sortDir);
        PagedResponse<UserDto> response = userService.listUsers(params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id) {
        UserDto user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update user details, role or status")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        UserDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "User updated successfully"));
    }

    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Unlock account locked by consecutive failed attempts (BR-20)")
    public ResponseEntity<ApiResponse<UserDto>> unlockUser(@PathVariable Long id) {
        UserDto unlocked = userService.unlockUser(id);
        return ResponseEntity.ok(ApiResponse.ok(unlocked, "User account unlocked successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Soft delete user (BR-18)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "User deleted successfully"));
    }

    @PostMapping("/invite-supplier")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PROCUREMENT_OFFICER')")
    @Operation(summary = "Invite and provision supplier portal user")
    public ResponseEntity<ApiResponse<UserDto>> inviteSupplier(@Valid @RequestBody InviteSupplierRequest request) {
        UserDto invited = userService.inviteSupplierUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(invited));
    }
}
