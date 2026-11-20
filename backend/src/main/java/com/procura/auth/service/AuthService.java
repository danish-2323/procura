package com.procura.auth.service;

import com.procura.auth.domain.RefreshToken;
import com.procura.auth.dto.ChangePasswordRequest;
import com.procura.auth.dto.LoginRequest;
import com.procura.auth.dto.LoginResponse;
import com.procura.auth.dto.RefreshTokenRequest;
import com.procura.auth.repo.RefreshTokenRepository;
import com.procura.security.CurrentUser;
import com.procura.security.JwtService;
import com.procura.shared.audit.AuditService;
import com.procura.shared.audit.AuditableAction;
import com.procura.shared.exception.ApiErrorCode;
import com.procura.shared.exception.BusinessRuleException;
import com.procura.shared.exception.UnauthorizedException;
import com.procura.user.domain.User;
import com.procura.user.dto.UserDto;
import com.procura.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Value("${app.jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    @Value("${app.jwt.expiration-minutes:15}")
    private long accessExpirationMinutes;

    @Transactional
    public LoginResponse login(LoginRequest req, String clientIp, String traceId) {
        Optional<User> userOpt = userRepository.findByEmailAndIsDeletedFalse(req.email().trim().toLowerCase());

        if (userOpt.isEmpty()) {
            auditService.recordStandalone(
                null, null, AuditableAction.LOGIN_FAILED, "USER", 0L,
                null, "Attempted email: " + req.email(), clientIp, traceId
            );
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userOpt.get();

        if (user.isAccountLocked()) {
            auditService.recordStandalone(
                user.getId(), null, AuditableAction.LOGIN_FAILED, "USER", user.getId(),
                null, "Account locked until: " + user.getLockedUntil(), clientIp, traceId
            );
            throw new BusinessRuleException(
                "BR-20",
                ApiErrorCode.UNAUTHENTICATED,
                "Account is temporarily locked due to 5 consecutive failed login attempts. Please try again after 15 minutes."
            );
        }

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            user.registerFailedLogin();
            userRepository.save(user);

            auditService.recordStandalone(
                user.getId(), null, AuditableAction.LOGIN_FAILED, "USER", user.getId(),
                "attempts=" + (user.getFailedAttempts() - 1), "attempts=" + user.getFailedAttempts(),
                clientIp, traceId
            );

            if (user.isAccountLocked()) {
                auditService.recordStandalone(
                    user.getId(), null, AuditableAction.USER_LOCKED, "USER", user.getId(),
                    null, "Locked until: " + user.getLockedUntil(), clientIp, traceId
                );
            }

            throw new BadCredentialsException("Invalid email or password");
        }

        // Login success: reset failed counter
        user.resetFailedLogins();
        userRepository.save(user);

        CurrentUser currentUser = toCurrentUser(user);
        String accessToken = jwtService.generateAccessToken(currentUser);
        String rawRefreshToken = UUID.randomUUID().toString();
        String hashedRefresh = hashToken(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .tokenHash(hashedRefresh)
            .expiresAt(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS))
            .build();
        refreshTokenRepository.save(refreshToken);

        auditService.recordStandalone(
            user.getId(), null, AuditableAction.LOGIN_SUCCESS, "USER", user.getId(),
            null, "Login successful", clientIp, traceId
        );

        return new LoginResponse(
            accessToken,
            rawRefreshToken,
            accessExpirationMinutes * 60,
            UserDto.from(user)
        );
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest req, String clientIp, String traceId) {
        String tokenHash = hashToken(req.refreshToken());
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(tokenHash);

        if (tokenOpt.isEmpty()) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        RefreshToken token = tokenOpt.get();
        User user = token.getUser();

        // REUSE DETECTION: If token is already revoked, compromise detected!
        if (token.isRevoked()) {
            log.warn("Revoked refresh token presented for user {}. Revoking all tokens!", user.getId());
            refreshTokenRepository.revokeAllForUser(user.getId(), Instant.now());
            auditService.recordStandalone(
                user.getId(), null, AuditableAction.LOGIN_FAILED, "TOKEN", token.getId(),
                "REUSE_DETECTED", "All tokens revoked", clientIp, traceId
            );
            throw new UnauthorizedException("Refresh token reuse detected. All sessions have been revoked.");
        }

        if (token.isExpired()) {
            throw new UnauthorizedException("Refresh token has expired. Please log in again.");
        }

        // Rotate token
        token.revoke();
        refreshTokenRepository.save(token);

        String newRawRefreshToken = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = RefreshToken.builder()
            .user(user)
            .tokenHash(hashToken(newRawRefreshToken))
            .expiresAt(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS))
            .build();
        refreshTokenRepository.save(newRefreshToken);

        CurrentUser currentUser = toCurrentUser(user);
        String newAccessToken = jwtService.generateAccessToken(currentUser);

        return new LoginResponse(
            newAccessToken,
            newRawRefreshToken,
            accessExpirationMinutes * 60,
            UserDto.from(user)
        );
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            String tokenHash = hashToken(rawRefreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(RefreshToken::revoke);
        }
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req, String clientIp, String traceId) {
        if (req.newPassword().length() < 10) {
            throw new BusinessRuleException("BR-20", "Password must be at least 10 characters long");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens on password change
        refreshTokenRepository.revokeAllForUser(user.getId(), Instant.now());

        auditService.recordStandalone(
            user.getId(), null, AuditableAction.PASSWORD_CHANGED, "USER", user.getId(),
            null, "Password changed successfully", clientIp, traceId
        );
    }

    private CurrentUser toCurrentUser(User user) {
        return CurrentUser.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole().getName())
            .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
            .warehouseId(user.getWarehouse() != null ? user.getWarehouse().getId() : null)
            .active(user.getStatus() == com.procura.user.domain.UserStatus.ACTIVE)
            .build();
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
