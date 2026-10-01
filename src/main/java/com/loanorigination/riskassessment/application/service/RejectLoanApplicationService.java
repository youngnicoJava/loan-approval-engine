package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.application.port.in.RejectLoanApplicationUseCase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;
import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;

@ApplicationScoped
public class RejectLoanApplicationService implements RejectLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final WorkflowEventRecorder events;

    @Inject
    public RejectLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository,
            WorkflowEventRecorder events
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.events = events;
    }

    @Override
    @Transactional
    public LoanApplication reject(
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

        loanApplication.reject(
                Instant.now()
        );

        loanApplicationRepository.save(
                loanApplication
        );
        events.record("LoanApplicationRejected", AuditAction.APPLICATION_REJECTED, "LoanApplication", loanApplicationId,
                java.util.Map.of("loanApplicationId", loanApplicationId.toString(), "source", "MANUAL_REVIEW"));

        return loanApplication;
    }
}
