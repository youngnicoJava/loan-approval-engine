package com.loanorigination.outbox.application.port.out;

import com.loanorigination.outbox.domain.IntegrationEvent;

public interface EventPublisherPort {
    void publish(IntegrationEvent event);
}
