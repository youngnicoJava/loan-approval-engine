package com.loanorigination.idempotency.application.exception;

public class IdempotencyKeyRequiredException extends IdempotencyException {
    public IdempotencyKeyRequiredException() { super("Idempotency-Key header is required"); }
}
