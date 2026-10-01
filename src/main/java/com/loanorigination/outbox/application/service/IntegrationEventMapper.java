package com.loanorigination.outbox.application.service;

import com.loanorigination.outbox.domain.IntegrationEvent;
import com.loanorigination.outbox.domain.OutboxEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IntegrationEventMapper {
    public IntegrationEvent from(OutboxEvent event){
        return new IntegrationEvent(event.eventId(),event.eventType()+".v"+event.eventVersion(),
                event.aggregateType(),event.aggregateId(),event.correlationId(),event.payload(),event.occurredAt());
    }
}
