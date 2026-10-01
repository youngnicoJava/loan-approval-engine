package com.loanorigination.idempotency.application.exception;
public abstract class IdempotencyException extends RuntimeException {
 protected IdempotencyException(String message){super(message);}
}
