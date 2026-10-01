package com.loanorigination.idempotency.domain;

import java.time.Instant;
import java.util.UUID;

public record IdempotencyRecord(UUID id, String actorId, String scope, String key, String requestHash,
                                Integer responseStatus, String responseBody, IdempotencyStatus status,
                                Instant createdAt, Instant expiresAt) {
    public boolean expiredAt(Instant now) { return !expiresAt.isAfter(now); }
}
