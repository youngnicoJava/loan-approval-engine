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
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class CreateLoanApplicationService
        implements CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUserPort currentUserPort;

    @Inject
    public CreateLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository,
            CustomerRepository customerRepository,
            CurrentUserPort currentUserPort
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.customerRepository = customerRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    @Transactional
    public LoanApplication create(
            CreateLoanApplicationCommand command
    ) {

        String externalIdentityId = currentUserPort
                .getCurrentUser()
                .externalIdentityId();

        Customer customer = customerRepository
                .findByExternalIdentityId(externalIdentityId)
                .orElseThrow(
                        () -> new CustomerNotFoundException(
                                externalIdentityId
                        )
                );

        if (customer.status() == CustomerStatus.BLOCKED) {
            throw new CustomerBlockedException(customer.id());
        }

        LoanApplication loanApplication = LoanApplication.create(
                customer.id(),
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