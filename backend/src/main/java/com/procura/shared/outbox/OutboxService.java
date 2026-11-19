package com.procura.shared.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void publishEvent(String eventType, String aggregateType, Long aggregateId, Object payloadObj) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payloadObj);
            OutboxEvent event = OutboxEvent.builder()
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payloadJson)
                .status(OutboxStatus.PENDING)
                .attempts(0)
                .build();

            outboxEventRepository.save(event);
            log.debug("Outbox event saved: type={}, aggregateId={}", eventType, aggregateId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox event payload", e);
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void dispatchPendingEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
            OutboxStatus.PENDING, PageRequest.of(0, 50)
        );

        for (OutboxEvent event : pending) {
            try {
                // Dispatch outbox event to internal Spring Application listeners
                applicationEventPublisher.publishEvent(event);
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setProcessedAt(Instant.now());
            } catch (Exception ex) {
                log.error("Failed to dispatch outbox event id={}", event.getId(), ex);
                event.setAttempts(event.getAttempts() + 1);
                if (event.getAttempts() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                }
            }
            outboxEventRepository.save(event);
        }
    }
}
