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

import java.time.Clock;
import java.util.UUID;
import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;

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
    private final WorkflowEventRecorder events;
    private final Clock clock;

    @Inject
    public FinalizeRiskAssessmentService(
            LoanApplicationRepository loanApplicationRepository,
            RiskAssessmentRepository riskAssessmentRepository,
            WorkflowEventRecorder events,
            Clock clock
    ) {
        this.loanApplicationRepository =
                loanApplicationRepository;
        this.riskAssessmentRepository =
                riskAssessmentRepository;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public RiskAssessment finalize(
            UUID loanApplicationId,
            RiskAssessmentResult result
    ) {

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findByIdForUpdate(loanApplicationId)
                        .orElseThrow(
                                () -> new LoanApplicationNotFoundException(
                                        loanApplicationId
                                )
                        );

        var now = clock.instant();
        RiskAssessment assessment =
                RiskAssessment.create(
                        loanApplicationId,
                        result,
                        now
                );

        riskAssessmentRepository.save(
                assessment
        );
        events.record("RiskAssessmentCompleted", AuditAction.RISK_ASSESSMENT_COMPLETED, "LoanApplication", loanApplicationId,
                java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "decision", result.decision().name()));

        switch (result.decision()) {

            case APPROVE -> {
                loanApplication.approve(now);
                events.record("LoanApplicationApproved", AuditAction.APPLICATION_APPROVED, "LoanApplication", loanApplicationId,
                        java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "source", "RISK_ASSESSMENT"));
            }

            case REJECT -> {
                loanApplication.reject(now);
                events.record("LoanApplicationRejected", AuditAction.APPLICATION_REJECTED, "LoanApplication", loanApplicationId,
                        java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "source", "RISK_ASSESSMENT"));
            }

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
