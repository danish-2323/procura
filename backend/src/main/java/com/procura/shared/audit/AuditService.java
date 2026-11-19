package com.procura.shared.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(
        Long userId,
        Long onBehalfOf,
        AuditableAction action,
        String entityType,
        Long entityId,
        String oldValue,
        String newValue,
        String ipAddress,
        String traceId
    ) {
        AuditLog entry = AuditLog.builder()
            .userId(userId)
            .onBehalfOf(onBehalfOf)
            .action(action.name())
            .entityType(entityType)
            .entityId(entityId)
            .oldValue(oldValue)
            .newValue(newValue)
            .ipAddress(ipAddress)
            .traceId(traceId)
            .build();

        auditLogRepository.save(entry);
        log.info("Audit entry committed: action={}, entityType={}, entityId={}, userId={}",
            action, entityType, entityId, userId);
    }

    @Transactional
    public void recordStandalone(
        Long userId,
        Long onBehalfOf,
        AuditableAction action,
        String entityType,
        Long entityId,
        String oldValue,
        String newValue,
        String ipAddress,
        String traceId
    ) {
        AuditLog entry = AuditLog.builder()
            .userId(userId)
            .onBehalfOf(onBehalfOf)
            .action(action.name())
            .entityType(entityType)
            .entityId(entityId)
            .oldValue(oldValue)
            .newValue(newValue)
            .ipAddress(ipAddress)
            .traceId(traceId)
            .build();

        auditLogRepository.save(entry);
    }
}
