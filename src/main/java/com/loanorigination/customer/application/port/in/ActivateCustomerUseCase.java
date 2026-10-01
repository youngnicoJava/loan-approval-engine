package com.loanorigination.customer.application.port.in;

import com.loanorigination.customer.domain.Customer;

import java.util.UUID;

public interface ActivateCustomerUseCase {

    Customer activate(UUID customerId);
}