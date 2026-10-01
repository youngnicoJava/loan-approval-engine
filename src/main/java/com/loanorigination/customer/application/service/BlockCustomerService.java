package com.loanorigination.customer.application.service;

import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.in.BlockCustomerUseCase;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class BlockCustomerService implements BlockCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public BlockCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer block(UUID customerId) {

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.block();

        customerRepository.save(customer);

        return customer;
    }
}