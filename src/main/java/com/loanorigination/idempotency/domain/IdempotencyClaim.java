package com.loanorigination.idempotency.domain;

public record IdempotencyClaim(IdempotencyRecord record, boolean acquired) { }
