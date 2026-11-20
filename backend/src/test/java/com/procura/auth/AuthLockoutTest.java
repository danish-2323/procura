package com.procura.auth;

import com.procura.auth.dto.ChangePasswordRequest;
import com.procura.auth.dto.LoginRequest;
import com.procura.auth.service.AuthService;
import com.procura.shared.exception.BusinessRuleException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthLockoutTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "lockout.test@procura.local";
    private static final String CORRECT_PASSWORD = "ValidPassword123!";

    @BeforeEach
    void setupUser() {
        Role role = roleRepository.findByName("EMPLOYEE")
            .orElseGet(() -> roleRepository.save(Role.builder().name("EMPLOYEE").build()));

        User user = User.builder()
            .email(TEST_EMAIL)
            .passwordHash(passwordEncoder.encode(CORRECT_PASSWORD))
            .fullName("Lockout Test User")
            .role(role)
            .status(UserStatus.ACTIVE)
            .failedAttempts(0)
            .isDeleted(false)
            .build();

        userRepository.save(user);
    }

    @Test
    @DisplayName("BR-20: 5 consecutive failed logins lock account for 15 minutes")
    void br20_passwordRulesAndLockout() {
        LoginRequest wrongLogin = new LoginRequest(TEST_EMAIL, "WrongPassword!");

        // 4 failed attempts should throw BadCredentialsException
        for (int i = 1; i <= 4; i++) {
            final int attempt = i;
            assertThatThrownBy(() -> authService.login(wrongLogin, "127.0.0.1", "trace-" + attempt))
                .isInstanceOf(BadCredentialsException.class);

            User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
            assertThat(user.getFailedAttempts()).isEqualTo(attempt);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        // 5th failed attempt locks the account
        assertThatThrownBy(() -> authService.login(wrongLogin, "127.0.0.1", "trace-5"))
            .isInstanceOf(BadCredentialsException.class);

        User lockedUser = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertThat(lockedUser.getFailedAttempts()).isEqualTo(5);
        assertThat(lockedUser.getStatus()).isEqualTo(UserStatus.LOCKED);
        assertThat(lockedUser.getLockedUntil()).isNotNull();

        // 6th attempt (even with CORRECT password) must be rejected with BR-20 BusinessRuleException
        LoginRequest correctLogin = new LoginRequest(TEST_EMAIL, CORRECT_PASSWORD);
        assertThatThrownBy(() -> authService.login(correctLogin, "127.0.0.1", "trace-6"))
            .isInstanceOf(BusinessRuleException.class)
            .satisfies(ex -> {
                BusinessRuleException bre = (BusinessRuleException) ex;
                assertThat(bre.getRuleId()).isEqualTo("BR-20");
            });
    }

    @Test
    @DisplayName("BR-20: Password change rejects passwords shorter than 10 characters")
    void br20_passwordMinimumLength() {
        User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        ChangePasswordRequest shortPassword = new ChangePasswordRequest(CORRECT_PASSWORD, "Short9!");

        assertThatThrownBy(() -> authService.changePassword(user.getId(), shortPassword, "127.0.0.1", "trace-change"))
            .isInstanceOf(BusinessRuleException.class)
            .satisfies(ex -> {
                BusinessRuleException bre = (BusinessRuleException) ex;
                assertThat(bre.getRuleId()).isEqualTo("BR-20");
            });
    }
}
