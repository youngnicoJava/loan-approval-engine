package com.loanorigination.fraudassessment.application;

import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;
import com.loanorigination.fraudassessment.domain.FraudCaseResolutionRecord;
import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.idempotency.application.exception.IdempotencyKeyReusedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;

@ApplicationScoped
public class ApplyFraudCaseResolutionService {
    private final FraudCaseResolutionRepository resolutions;
    private final LoanApplicationRepository applications;
    private final FraudAssessmentGateService gate;
    private final WorkflowEventRecorder events;

    @Inject
    public ApplyFraudCaseResolutionService(FraudCaseResolutionRepository resolutions,
                                           LoanApplicationRepository applications,
                                           FraudAssessmentGateService gate,
                                           WorkflowEventRecorder events) {
        this.resolutions = resolutions;
        this.applications = applications;
        this.gate = gate;
        this.events = events;
    }

    @Transactional
    public void apply(FraudCaseResolutionRecord resolution) {
        var existing = resolutions.findByFraudCaseId(resolution.fraudCaseId());
        if (existing.isPresent()) {
            if (!existing.get().equals(resolution)) {
                throw new IdempotencyKeyReusedException();
            }
            return;
        }
        var application = applications.findByIdForUpdate(resolution.loanApplicationId())
                .orElseThrow(() -> new LoanApplicationNotFoundException(resolution.loanApplicationId()));
        resolutions.save(resolution);
        events.record("FraudCaseResolved", AuditAction.FRAUD_CASE_RESOLVED, "LoanApplication",
                application.id(), Map.of("fraudCaseId", resolution.fraudCaseId().toString(),
                        "fraudAssessmentId", resolution.fraudAssessmentId().toString(),
                        "resolution", resolution.resolution()));
        if ("CLEARED".equals(resolution.resolution())) {
            gate.approveIfBothAutomatedGatesPass(application);
        }
    }
}
