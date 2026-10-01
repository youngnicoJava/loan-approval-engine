package com.loanorigination.outbox.application.port.out;

import com.loanorigination.outbox.domain.OutboxEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository {
    void append(OutboxEvent event);
    List<UUID> claimBatch(Instant now, Instant expiredLease, int limit);
    OutboxEvent findById(UUID id);
    void markPublished(UUID id, Instant at);
    void markFailed(UUID id, String reason);
}
