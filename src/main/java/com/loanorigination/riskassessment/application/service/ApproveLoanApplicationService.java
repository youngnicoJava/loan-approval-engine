package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.application.port.in.ApproveLoanApplicationUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.UUID;
import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;

@ApplicationScoped
public class ApproveLoanApplicationService
        implements ApproveLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final WorkflowEventRecorder events;
    private final Clock clock;
    private final com.loanorigination.fraudassessment.application.FraudAssessmentGateService fraudGate;

    @Inject
    public ApproveLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository,
            WorkflowEventRecorder events,
            Clock clock,
            com.loanorigination.fraudassessment.application.FraudAssessmentGateService fraudGate
    ) {
        this.loanApplicationRepository =
                loanApplicationRepository;
        this.events = events;
        this.clock = clock;
        this.fraudGate = fraudGate;
    }

    @Override
    @Transactional
    public LoanApplication approve(
            UUID loanApplicationId
    ) {

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findByIdForUpdate(loanApplicationId)
                        .orElseThrow(
                                () -> new LoanApplicationNotFoundException(
                                        loanApplicationId
                                )
                        );

        fraudGate.assertCleared(loanApplicationId);

        loanApplication.approve(
                clock.instant()
        );

        loanApplicationRepository.save(
                loanApplication
        );
        events.record("LoanApplicationApproved", AuditAction.APPLICATION_APPROVED, "LoanApplication", loanApplicationId,
                java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "source", "MANUAL_REVIEW"));

        return loanApplication;
    }
}
