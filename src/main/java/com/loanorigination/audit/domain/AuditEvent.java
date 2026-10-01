package com.loanorigination.audit.domain;

import java.time.Instant;
import java.util.UUID;

public record AuditEvent(UUID id, UUID eventId, String actorId, AuditActorType actorType,
                         AuditAction action, String aggregateType, UUID aggregateId,
                         Instant occurredAt, String correlationId, String metadata) { }
