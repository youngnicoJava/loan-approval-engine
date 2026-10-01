package com.loanorigination.customer.application.exception;

import java.util.UUID;

public class CustomerBlockedException extends RuntimeException {
    public CustomerBlockedException(UUID customerId) {
        super("Customer is blocked: " + customerId);
    }
}
