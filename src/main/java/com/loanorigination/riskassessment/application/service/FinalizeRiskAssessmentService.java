package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentRepository;
import com.loanorigination.riskassessment.domain.RiskAssessment;
import com.loanorigination.riskassessment.domain.RiskAssessmentResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Persiste el resultado de riesgo y aplica su efecto sobre el ciclo
 * de vida de LoanApplication.
 *
 * La operación es transaccional para que assessment + cambio de estado
 * sean persistidos como una única unidad.
 */
@ApplicationScoped
public class FinalizeRiskAssessmentService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;

    @Inject
    public FinalizeRiskAssessmentService(
            LoanApplicationRepository loanApplicationRepository,
            RiskAssessmentRepository riskAssessmentRepository
    ) {
        this.loanApplicationRepository =
                loanApplicationRepository;
        this.riskAssessmentRepository =
                riskAssessmentRepository;
    }

    @Transactional
    public RiskAssessment finalize(
            UUID loanApplicationId,
            RiskAssessmentResult result
    ) {

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findById(loanApplicationId)
                        .orElseThrow(
                                () -> new LoanApplicationNotFoundException(
                                        loanApplicationId
                                )
                        );

        RiskAssessment assessment =
                RiskAssessment.create(
                        loanApplicationId,
                        result,
                        Instant.now()
                );

        riskAssessmentRepository.save(
                assessment
        );

        switch (result.decision()) {

            case APPROVE ->
                    loanApplication.approve(Instant.now());

            case REJECT ->
                    loanApplication.reject(Instant.now());

            case REFER -> {
                /*
                 * REFER mantiene la solicitud UNDER_REVIEW.
                 * Esto permite que un Loan Officer tome la decisión final.
                 */
            }
        }

        loanApplicationRepository.save(
                loanApplication
        );

        return assessment;
    }
}