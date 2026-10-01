package com.loanorigination.loanapplication.application.service;

import com.loanorigination.loanapplication.application.command.CreateLoanApplicationCommand;
import com.loanorigination.loanapplication.application.port.in.CreateLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Orquesta el caso de uso de creación.
 *
 * El servicio coordina infraestructura y dominio, pero no duplica
 * las reglas que ya pertenecen al agregado LoanApplication.
 */
@ApplicationScoped
public class CreateLoanApplicationService implements CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    @Inject
    public CreateLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Override
    @Transactional
    public LoanApplication create(CreateLoanApplicationCommand command) {

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