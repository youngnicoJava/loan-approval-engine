package com.loanorigination.loanapplication.application.service;

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
public class SubmitLoanApplicationService implements SubmitLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    @Inject
    public SubmitLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Override
    @Transactional
    public LoanApplication submit(UUID loanApplicationId) {

        LoanApplication loanApplication = loanApplicationRepository
                .findById(loanApplicationId)
                .orElseThrow(() -> new LoanApplicationNotFoundException(loanApplicationId));

        /*
         * La transición DRAFT -> SUBMITTED pertenece al agregado.
         * El servicio solamente coordina la operación y la persistencia.
         */
        loanApplication.submit(Instant.now());

        loanApplicationRepository.save(loanApplication);

        return loanApplication;
    }
}