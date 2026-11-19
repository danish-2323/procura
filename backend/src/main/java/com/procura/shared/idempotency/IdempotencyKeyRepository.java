package com.procura.shared.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByUserIdAndHttpMethodAndEndpointAndKey(
        Long userId, String httpMethod, String endpoint, String key
    );

    void deleteByExpiresAtBefore(Instant now);
}
