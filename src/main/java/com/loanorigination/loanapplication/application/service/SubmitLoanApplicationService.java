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
import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.loanapplication.application.exception.LoanApplicationAccessDeniedException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class SubmitLoanApplicationService implements SubmitLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUserPort currentUserPort;

    @Inject
    public SubmitLoanApplicationService(
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
    public LoanApplication submit(UUID loanApplicationId) {

        LoanApplication loanApplication = loanApplicationRepository
                .findById(loanApplicationId)
                .orElseThrow(
                        () -> new LoanApplicationNotFoundException(
                                loanApplicationId
                        )
                );

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

        if (customer.id().equals(loanApplication.customerId())
                && customer.status() == CustomerStatus.BLOCKED) {

            throw new CustomerBlockedException(customer.id());
        }

        if (!customer.id().equals(loanApplication.customerId())) {
            throw new LoanApplicationAccessDeniedException(
                    loanApplicationId
            );
        }

        loanApplication.submit(Instant.now());

        loanApplicationRepository.save(loanApplication);

        return loanApplication;
    }
}