package com.loanorigination.idempotency.application.exception;

public class IdempotencyKeyReusedException extends IdempotencyException {
    public IdempotencyKeyReusedException() { super("IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_REQUEST"); }
}
