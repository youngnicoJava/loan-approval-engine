package com.loanorigination.shared.domain.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Hecho de dominio versionable, independiente del transporte que lo publique. */
public record DomainEvent(UUID eventId, String eventType, int eventVersion, Instant occurredAt,
                          String aggregateType, UUID aggregateId, String correlationId,
                          Map<String, Object> payload) {
    public DomainEvent {
        payload = Map.copyOf(payload);
        if (eventVersion < 1) throw new IllegalArgumentException("eventVersion must be positive");
    }
}
