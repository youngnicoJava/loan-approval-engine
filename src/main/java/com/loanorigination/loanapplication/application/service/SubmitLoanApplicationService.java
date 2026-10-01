package com.loanorigination.loanapplication.application.service;

import com.loanorigination.customer.application.exception.CustomerBlockedException;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.customer.domain.CustomerStatus;
import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.in.SubmitLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class SubmitLoanApplicationService
        implements SubmitLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;

    @Inject
    public SubmitLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository,
            CustomerRepository customerRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public LoanApplication submit(UUID loanApplicationId) {

        LoanApplication loanApplication = loanApplicationRepository
                .findById(loanApplicationId)
                .orElseThrow(
                        () -> new LoanApplicationNotFoundException(
                                loanApplicationId
                        )
                );

        Customer customer = customerRepository
                .findById(loanApplication.customerId())
                .orElseThrow(
                        () -> new CustomerNotFoundException(
                                loanApplication.customerId()
                        )
                );

        if (customer.status() == CustomerStatus.BLOCKED) {
            throw new CustomerBlockedException(customer.id());
        }

        /*
         * La transición DRAFT -> SUBMITTED sigue perteneciendo
         * exclusivamente al agregado LoanApplication.
         */
        loanApplication.submit(Instant.now());

        loanApplicationRepository.save(loanApplication);

        return loanApplication;
    }
}