package com.loanorigination.customer.application.service;

import com.loanorigination.customer.application.command.CreateCustomerCommand;
import com.loanorigination.customer.application.exception.CustomerAlreadyExistsException;
import com.loanorigination.customer.application.port.in.CreateCustomerUseCase;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer create(CreateCustomerCommand command) {

        if (customerRepository.existsByExternalIdentityId(
                command.externalIdentityId()
        )) {
            throw new CustomerAlreadyExistsException(
                    command.externalIdentityId()
            );
        }

        Customer customer = Customer.create(
                command.externalIdentityId(),
                command.fullName(),
                command.email(),
                Instant.now()
        );

        customerRepository.save(customer);

        return customer;
    }
}