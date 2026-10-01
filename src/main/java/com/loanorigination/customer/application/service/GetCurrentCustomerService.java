package com.loanorigination.customer.application.service;

import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.in.GetCurrentCustomerUseCase;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCurrentCustomerService
        implements GetCurrentCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CurrentUserPort currentUserPort;

    @Inject
    public GetCurrentCustomerService(
            CustomerRepository customerRepository,
            CurrentUserPort currentUserPort
    ) {
        this.customerRepository = customerRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public Customer getCurrentCustomer() {

        String externalIdentityId = currentUserPort
                .getCurrentUser()
                .externalIdentityId();

        return customerRepository
                .findByExternalIdentityId(externalIdentityId)
                .orElseThrow(
                        () -> new CustomerNotFoundException(
                                externalIdentityId
                        )
                );
    }
}