package com.procura.auth;

import com.procura.auth.domain.RefreshToken;
import com.procura.auth.dto.LoginRequest;
import com.procura.auth.dto.LoginResponse;
import com.procura.auth.dto.RefreshTokenRequest;
import com.procura.auth.repo.RefreshTokenRepository;
import com.procura.auth.service.AuthService;
import com.procura.shared.exception.UnauthorizedException;
import com.procura.user.domain.Role;
import com.procura.user.domain.User;
import com.procura.user.domain.UserStatus;
import com.procura.user.repo.RoleRepository;
import com.procura.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RefreshTokenTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "token.test@procura.local";
    private static final String TEST_PASSWORD = "ValidPassword123!";

    @BeforeEach
    void setupUser() {
        Role role = roleRepository.findByName("EMPLOYEE")
            .orElseGet(() -> roleRepository.save(Role.builder().name("EMPLOYEE").build()));

        User user = User.builder()
            .email(TEST_EMAIL)
            .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
            .fullName("Token Test User")
            .role(role)
            .status(UserStatus.ACTIVE)
            .failedAttempts(0)
            .isDeleted(false)
            .build();

        userRepository.save(user);
    }

    @Test
    @DisplayName("JWT: Refresh token rotation issues a new token and revokes the old one")
    void refreshTokenRotation() {
        LoginResponse loginResp = authService.login(
            new LoginRequest(TEST_EMAIL, TEST_PASSWORD), "127.0.0.1", "trace-rotate-1"
        );

        String oldRefreshToken = loginResp.refreshToken();
        assertThat(oldRefreshToken).isNotBlank();

        // Perform rotation
        LoginResponse refreshResp = authService.refreshToken(
            new RefreshTokenRequest(oldRefreshToken), "127.0.0.1", "trace-rotate-2"
        );

        assertThat(refreshResp.accessToken()).isNotBlank();
        assertThat(refreshResp.refreshToken()).isNotEqualTo(oldRefreshToken);

        // Old refresh token is now revoked
        User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        assertThat(tokens).hasSize(2);

        RefreshToken revokedToken = tokens.stream()
            .filter(RefreshToken::isRevoked)
            .findFirst()
            .orElseThrow();
        assertThat(revokedToken.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("JWT: Refresh token reuse detection revokes all user sessions")
    void refreshTokenReuseDetection() {
        LoginResponse loginResp = authService.login(
            new LoginRequest(TEST_EMAIL, TEST_PASSWORD), "127.0.0.1", "trace-reuse-1"
        );
        String initialToken = loginResp.refreshToken();

        // 1st rotation - valid
        LoginResponse rotationResp = authService.refreshToken(
            new RefreshTokenRequest(initialToken), "127.0.0.1", "trace-reuse-2"
        );
        String secondToken = rotationResp.refreshToken();

        // Attacker replays initialToken (which is already revoked)
        assertThatThrownBy(() -> authService.refreshToken(
            new RefreshTokenRequest(initialToken), "192.168.1.99", "trace-reuse-attack"
        ))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("Refresh token reuse detected");

        // The legitimate secondToken must now also be revoked due to compromise handling
        assertThatThrownBy(() -> authService.refreshToken(
            new RefreshTokenRequest(secondToken), "127.0.0.1", "trace-reuse-3"
        ))
            .isInstanceOf(UnauthorizedException.class);
    }
}
