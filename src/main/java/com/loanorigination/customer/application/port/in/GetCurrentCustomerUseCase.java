package com.loanorigination.customer.application.port.in;

import com.loanorigination.customer.domain.Customer;

public interface GetCurrentCustomerUseCase {

    Customer getCurrentCustomer();
}
