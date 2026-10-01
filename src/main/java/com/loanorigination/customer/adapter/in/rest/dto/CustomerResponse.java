package com.loanorigination.customer.adapter.in.rest.dto;

import com.loanorigination.customer.domain.Customer;
import com.loanorigination.customer.domain.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String externalIdentityId,
        String fullName,
        String email,
        CustomerStatus status,
        Instant createdAt
) {

    public static CustomerResponse from(Customer customer) {

        return new CustomerResponse(
                customer.id(),
                customer.externalIdentityId(),
                customer.fullName(),
                customer.email(),
                customer.status(),
                customer.createdAt()
        );
    }
}