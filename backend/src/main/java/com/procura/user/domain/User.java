package com.procura.user.domain;

import com.procura.department.domain.Department;
import com.procura.department.domain.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int LOCKOUT_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "failed_attempts", nullable = false)
    @Builder.Default
    private Integer failedAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    public boolean isAccountLocked() {
        if (status == UserStatus.LOCKED) {
            if (lockedUntil != null && lockedUntil.isAfter(Instant.now())) {
                return true;
            }
            // Lockout period has elapsed; auto-recover
            this.status = UserStatus.ACTIVE;
            this.failedAttempts = 0;
            this.lockedUntil = null;
        }
        return false;
    }

    public void registerFailedLogin() {
        this.failedAttempts++;
        if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
            this.status = UserStatus.LOCKED;
            this.lockedUntil = Instant.now().plus(LOCKOUT_MINUTES, ChronoUnit.MINUTES);
        }
        this.updatedAt = Instant.now();
    }

    public void resetFailedLogins() {
        this.failedAttempts = 0;
        this.lockedUntil = null;
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
        }
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
