package com.loanorigination.idempotency.application.port.out;

import com.loanorigination.idempotency.domain.IdempotencyClaim;
import com.loanorigination.idempotency.domain.IdempotencyRecord;
import java.time.Instant;

public interface IdempotencyRepository {
    IdempotencyClaim claim(String actorId, String scope, String key, String requestHash, Instant now, Instant expiresAt);
    void complete(IdempotencyRecord record, int responseStatus, String responseBody);
    void delete(IdempotencyRecord record);
}
