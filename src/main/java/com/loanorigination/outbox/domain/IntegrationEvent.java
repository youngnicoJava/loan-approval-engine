package com.loanorigination.outbox.domain;

import java.time.Instant;
import java.util.UUID;

/** Contrato externo versionado; su forma puede evolucionar sin modificar los eventos de dominio. */
public record IntegrationEvent(UUID eventId,String eventType,String aggregateType,UUID aggregateId,
                               String correlationId,String payload,Instant occurredAt) { }
