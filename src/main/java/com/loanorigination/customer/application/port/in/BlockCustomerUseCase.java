package com.loanorigination.customer.application.port.in;

import com.loanorigination.customer.domain.Customer;

import java.util.UUID;

public interface BlockCustomerUseCase {

    Customer block(UUID customerId);
}