package com.loanorigination.customer.application.port.in;

import com.loanorigination.customer.application.command.CreateCustomerCommand;
import com.loanorigination.customer.domain.Customer;

public interface CreateCustomerUseCase {

    Customer create(CreateCustomerCommand command);
}