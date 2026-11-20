package com.procura.config.api;

import com.procura.config.dto.ConfigDto;
import com.procura.config.dto.UpdateConfigSettingRequest;
import com.procura.config.service.ConfigService;
import com.procura.security.CurrentUser;
import com.procura.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
@Tag(name = "Configuration Settings", description = "System-wide parameters such as tolerances and thresholds")
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    @Operation(summary = "Get all system configuration parameters")
    public ResponseEntity<ApiResponse<List<ConfigDto>>> getAllConfig() {
        List<ConfigDto> settings = configService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.ok(settings));
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update system setting (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<ConfigDto>> updateConfig(
        @AuthenticationPrincipal CurrentUser principal,
        @Valid @RequestBody UpdateConfigSettingRequest request,
        HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);
        String traceId = getOrCreateTraceId(httpRequest);
        ConfigDto updated = configService.updateSetting(request, principal != null ? principal.getId() : null, clientIp, traceId);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Configuration setting updated"));
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
