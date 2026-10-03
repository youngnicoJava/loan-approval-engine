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
    private final com.loanorigination.fraudassessment.application.FraudAssessmentGateService fraudGate;

    @Inject
    public FinalizeRiskAssessmentService(
            LoanApplicationRepository loanApplicationRepository,
            RiskAssessmentRepository riskAssessmentRepository,
            WorkflowEventRecorder events,
            Clock clock,
            com.loanorigination.fraudassessment.application.FraudAssessmentGateService fraudGate
    ) {
        this.loanApplicationRepository =
                loanApplicationRepository;
        this.riskAssessmentRepository =
                riskAssessmentRepository;
        this.events = events;
        this.clock = clock;
        this.fraudGate = fraudGate;
    }

    @Transactional
    public RiskAssessment finalize(
            UUID loanApplicationId,
            RiskAssessmentResult result
    ) {
        return finalizeWithId(UUID.randomUUID(), loanApplicationId, result);
    }

    @Transactional
    public RiskAssessment finalizeExternal(UUID assessmentId, UUID loanApplicationId, RiskAssessmentResult result) {
        return finalizeWithId(assessmentId, loanApplicationId, result);
    }

    private RiskAssessment finalizeWithId(UUID assessmentId, UUID loanApplicationId, RiskAssessmentResult result) {

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findByIdForUpdate(loanApplicationId)
                        .orElseThrow(
                                () -> new LoanApplicationNotFoundException(
                                        loanApplicationId
                                )
                        );

        var duplicate = riskAssessmentRepository.findById(assessmentId);
        if (duplicate.isPresent()) {
            if (!duplicate.get().loanApplicationId().equals(loanApplicationId)) {
                throw new IllegalArgumentException("Risk assessment event does not match its application");
            }
            return duplicate.get();
        }

        var now = clock.instant();
        RiskAssessment assessment =
                RiskAssessment.create(
                        assessmentId,
                        loanApplicationId,
                        result,
                        now
                );

        riskAssessmentRepository.save(
                assessment
        );
        events.record("RiskAssessmentCompleted", AuditAction.RISK_ASSESSMENT_COMPLETED, "LoanApplication", loanApplicationId,
                java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "decision", result.decision().name()));

        if (loanApplication.status() != com.loanorigination.loanapplication.domain.LoanApplicationStatus.UNDER_REVIEW) {
            return assessment;
        }

        switch (result.decision()) {

            case APPROVE -> {
                if (fraudGate.required()) {
                    fraudGate.approveIfBothAutomatedGatesPass(loanApplication);
                } else {
                    loanApplication.approve(now);
                    events.record("LoanApplicationApproved", AuditAction.APPLICATION_APPROVED, "LoanApplication", loanApplicationId,
                            java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "source", "RISK_ASSESSMENT"));
                }
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
