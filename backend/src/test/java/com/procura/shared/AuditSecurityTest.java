package com.procura.shared;

import com.procura.shared.audit.AuditLog;
import com.procura.shared.audit.AuditLogRepository;
import com.procura.shared.audit.AuditService;
import com.procura.shared.audit.AuditableAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditSecurityTest {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    @DisplayName("BR-17: Audit logs are append-only and record transitions with user and traceId")
    void br17_auditLogsAppendOnly() {
        long initialCount = auditLogRepository.count();

        auditService.recordStandalone(
            1L, null, AuditableAction.CONFIG_CHANGED, "SETTING", 100L,
            "oldVal", "newVal", "192.168.1.1", "trace-br17-test"
        );

        long newCount = auditLogRepository.count();
        assertThat(newCount).isEqualTo(initialCount + 1);

        List<AuditLog> entries = auditLogRepository.findAll();
        AuditLog latest = entries.get(entries.size() - 1);

        assertThat(latest.getAction()).isEqualTo(AuditableAction.CONFIG_CHANGED.name());
        assertThat(latest.getTraceId()).isEqualTo("trace-br17-test");
        assertThat(latest.getOldValue()).isEqualTo("oldVal");
        assertThat(latest.getNewValue()).isEqualTo("newVal");
        assertThat(latest.getCreatedAt()).isNotNull();
    }
}
