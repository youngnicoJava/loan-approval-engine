package com.loanorigination.customer.application.exception;

import java.util.UUID;

public class CustomerAccessDeniedException extends RuntimeException {

    public CustomerAccessDeniedException(UUID customerId) {
        super("Access denied for customer: " + customerId);
    }
}