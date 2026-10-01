package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Prepara la solicitud para ser evaluada.
 *
 * Esta operación transaccional termina antes de invocar el RiskAssessmentPort.
 * Esto evita mantener una transacción de PostgreSQL abierta mientras un
 * futuro adapter HTTP espera a otro servicio.
 */
@ApplicationScoped
public class PrepareLoanApplicationForRiskService {

    private final LoanApplicationRepository loanApplicationRepository;

    @Inject
    public PrepareLoanApplicationForRiskService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository =
                loanApplicationRepository;
    }

    @Transactional
    public LoanApplication prepare(
            UUID loanApplicationId
    ) {

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findById(loanApplicationId)
                        .orElseThrow(
                                () -> new LoanApplicationNotFoundException(
                                        loanApplicationId
                                )
                        );

        loanApplication.startReview(Instant.now());

        loanApplicationRepository.save(
                loanApplication
        );

        return loanApplication;
    }
}