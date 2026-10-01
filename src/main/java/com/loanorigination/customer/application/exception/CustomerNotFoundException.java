package com.loanorigination.customer.application.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found: " + customerId);
    }

    public CustomerNotFoundException(String externalIdentityId) {
        super("Customer not found for external identity: " + externalIdentityId);
    }
}