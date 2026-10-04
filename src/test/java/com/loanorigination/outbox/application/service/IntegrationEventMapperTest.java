package com.loanorigination.outbox.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loanorigination.outbox.domain.OutboxEvent;
import com.loanorigination.outbox.domain.OutboxStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class IntegrationEventMapperTest {
    private final IntegrationEventMapper mapper = new IntegrationEventMapper();

    @Test
    void doesNotAppendTheVersionTwiceWhenTheStoredTypeAlreadyIncludesIt() {
        var event = event("loan.risk-assessment.requested.v2", 2);

        assertEquals("loan.risk-assessment.requested.v2", mapper.from(event).eventType());
    }

    @Test
    void addsTheVersionToAnUnversionedStoredType() {
        var event = event("loan.fraud-assessment.requested", 1);

        assertEquals("loan.fraud-assessment.requested.v1", mapper.from(event).eventType());
    }

    private OutboxEvent event(String eventType, int version) {
        return new OutboxEvent(UUID.randomUUID(), UUID.randomUUID(), eventType, version,
                "LoanApplication", UUID.randomUUID(), "test-correlation", "{}", Instant.EPOCH,
                null, OutboxStatus.PENDING, 0, null, null);
    }
}
