package com.loanorigination.loanapplication.application.service;

import com.loanorigination.customer.application.exception.CustomerBlockedException;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.customer.domain.CustomerStatus;
import com.loanorigination.loanapplication.application.command.CreateLoanApplicationCommand;
import com.loanorigination.loanapplication.application.port.in.CreateLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class CreateLoanApplicationService
        implements CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;

    @Inject
    public CreateLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository,
            CustomerRepository customerRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public LoanApplication create(
            CreateLoanApplicationCommand command
    ) {

        Customer customer = customerRepository
                .findById(command.customerId())
                .orElseThrow(
                        () -> new CustomerNotFoundException(
                                command.customerId()
                        )
                );

        if (customer.status() == CustomerStatus.BLOCKED) {
            throw new CustomerBlockedException(customer.id());
        }

        LoanApplication loanApplication = LoanApplication.create(
                command.customerId(),
                command.productType(),
                command.requestedAmount(),
                command.term(),
                command.purpose(),
                Instant.now()
        );

        loanApplicationRepository.save(loanApplication);

        return loanApplication;
    }
}