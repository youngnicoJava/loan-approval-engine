package com.loanorigination.outbox.application.service;

import com.loanorigination.outbox.domain.IntegrationEvent;
import com.loanorigination.outbox.domain.OutboxEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IntegrationEventMapper {
    public IntegrationEvent from(OutboxEvent event){
        String versionSuffix = ".v" + event.eventVersion();
        String eventType = event.eventType().endsWith(versionSuffix)
                ? event.eventType()
                : event.eventType() + versionSuffix;
        return new IntegrationEvent(event.eventId(),eventType,
                event.aggregateType(),event.aggregateId(),event.correlationId(),event.payload(),event.occurredAt());
    }
}
