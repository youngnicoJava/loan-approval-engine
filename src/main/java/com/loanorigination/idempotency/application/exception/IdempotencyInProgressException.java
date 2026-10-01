package com.loanorigination.idempotency.application.exception;

public class IdempotencyInProgressException extends IdempotencyException {
    public IdempotencyInProgressException() { super("CONCURRENT_OPERATION"); }
}
