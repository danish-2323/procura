package com.procura.auth.api;

import com.procura.auth.dto.ChangePasswordRequest;
import com.procura.auth.dto.LoginRequest;
import com.procura.auth.dto.LoginResponse;
import com.procura.auth.dto.RefreshTokenRequest;
import com.procura.auth.service.AuthService;
import com.procura.security.CurrentUser;
import com.procura.shared.response.ApiResponse;
import com.procura.user.dto.UserDto;
import com.procura.user.repo.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login, refresh rotation, logout, and credentials")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user with email and password")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);
        String traceId = getOrCreateTraceId(httpRequest);
        LoginResponse response = authService.login(request, clientIp, traceId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue new access token")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request,
        HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);
        String traceId = getOrCreateTraceId(httpRequest);
        LoginResponse response = authService.refreshToken(request, clientIp, traceId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke session refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null) {
            authService.logout(request.refreshToken());
        }
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get profile of currently authenticated user")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal CurrentUser principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        UserDto userDto = userRepository.findById(principal.getId())
            .map(UserDto::from)
            .orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(userDto));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password (requires minimum 10 chars, BR-20)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
        @AuthenticationPrincipal CurrentUser principal,
        @Valid @RequestBody ChangePasswordRequest request,
        HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);
        String traceId = getOrCreateTraceId(httpRequest);
        authService.changePassword(principal.getId(), request, clientIp, traceId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully"));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private String getOrCreateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        return (traceId != null && !traceId.isBlank()) ? traceId : UUID.randomUUID().toString();
    }
}
