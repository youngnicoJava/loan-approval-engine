package com.loanorigination.loanapplication.application.service;

import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.loanapplication.application.exception.LoanApplicationAccessDeniedException;
import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.in.GetLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.security.domain.ApplicationRole;
import com.loanorigination.security.domain.AuthenticatedUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class GetLoanApplicationService
        implements GetLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUserPort currentUserPort;

    @Inject
    public GetLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository,
            CustomerRepository customerRepository,
            CurrentUserPort currentUserPort
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.customerRepository = customerRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public LoanApplication get(UUID loanApplicationId) {

        LoanApplication loanApplication = loanApplicationRepository
                .findById(loanApplicationId)
                .orElseThrow(
                        () -> new LoanApplicationNotFoundException(
                                loanApplicationId
                        )
                );

        AuthenticatedUser currentUser =
                currentUserPort.getCurrentUser();

        /*
         * Los perfiles operativos necesitan consultar solicitudes
         * independientemente de su Customer propietario.
         */
        if (currentUser.hasRole(ApplicationRole.LOAN_OFFICER)
                || currentUser.hasRole(ApplicationRole.AUDITOR)
                || currentUser.hasRole(ApplicationRole.ADMIN)) {
            return loanApplication;
        }

        /*
         * Un CUSTOMER solamente puede acceder a solicitudes propias.
         * El rol por sí solo no demuestra ownership del recurso.
         */
        Customer customer = customerRepository
                .findByExternalIdentityId(
                        currentUser.externalIdentityId()
                )
                .orElseThrow(
                        () -> new CustomerNotFoundException(
                                currentUser.externalIdentityId()
                        )
                );

        if (!customer.id().equals(loanApplication.customerId())) {
            throw new LoanApplicationAccessDeniedException(
                    loanApplicationId
            );
        }

        return loanApplication;
    }
}