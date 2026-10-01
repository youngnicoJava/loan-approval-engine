package com.loanorigination.outbox.domain;

import java.time.Instant;
import java.util.UUID;

public record OutboxEvent(UUID id, UUID eventId, String eventType, int eventVersion,
                          String aggregateType, UUID aggregateId, String correlationId,
                          String payload, Instant occurredAt, Instant publishedAt,
                          OutboxStatus status, int attemptCount, Instant lockedAt, String lastError) { }
