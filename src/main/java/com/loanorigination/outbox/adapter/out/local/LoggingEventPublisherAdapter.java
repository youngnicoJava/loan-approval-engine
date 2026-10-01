package com.loanorigination.outbox.adapter.out.local;

import com.loanorigination.outbox.application.port.out.EventPublisherPort;
import com.loanorigination.outbox.domain.IntegrationEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LoggingEventPublisherAdapter implements EventPublisherPort {
    private static final Logger LOG=Logger.getLogger(LoggingEventPublisherAdapter.class);
    @Override public void publish(IntegrationEvent event){LOG.infof("Published integration event eventId=%s eventType=%s aggregateId=%s correlationId=%s",event.eventId(),event.eventType(),event.aggregateId(),event.correlationId());}
}
