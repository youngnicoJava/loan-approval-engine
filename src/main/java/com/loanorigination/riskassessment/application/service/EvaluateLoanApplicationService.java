package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.application.port.in.EvaluateLoanApplicationUseCase;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentPort;
import com.loanorigination.riskassessment.domain.RiskAssessment;
import com.loanorigination.riskassessment.domain.RiskAssessmentResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

/**
 * Orquesta la evaluación completa.
 *
 * El caso de uso:
 *
 * 1. mueve la solicitud a UNDER_REVIEW;
 * 2. ejecuta el motor de riesgo fuera de una transacción DB;
 * 3. persiste el resultado y aplica la decisión en una nueva transacción.
 */
@ApplicationScoped
public class EvaluateLoanApplicationService implements EvaluateLoanApplicationUseCase {

    private final PrepareLoanApplicationForRiskService prepareService;
    private final FinalizeRiskAssessmentService finalizeService;
    private final RiskAssessmentPort riskAssessmentPort;

    @Inject
    public EvaluateLoanApplicationService(
            PrepareLoanApplicationForRiskService prepareService,
            FinalizeRiskAssessmentService finalizeService,
            RiskAssessmentPort riskAssessmentPort
    ) {
        this.prepareService = prepareService;
        this.finalizeService = finalizeService;
        this.riskAssessmentPort = riskAssessmentPort;
    }

    @Override
    public RiskAssessment evaluate(
            UUID loanApplicationId
    ) {

        LoanApplication loanApplication =
                prepareService.prepare(
                        loanApplicationId
                );

        RiskAssessmentResult result =
                riskAssessmentPort.evaluate(
                        loanApplication
                );

        return finalizeService.finalize(
                loanApplicationId,
                result
        );
    }
}