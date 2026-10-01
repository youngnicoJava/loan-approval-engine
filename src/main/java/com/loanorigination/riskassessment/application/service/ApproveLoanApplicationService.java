package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.application.port.in.ApproveLoanApplicationUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class ApproveLoanApplicationService
        implements ApproveLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    @Inject
    public ApproveLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository =
                loanApplicationRepository;
    }

    @Override
    @Transactional
    public LoanApplication approve(
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

        loanApplication.approve(
                Instant.now()
        );

        loanApplicationRepository.save(
                loanApplication
        );

        return loanApplication;
    }
}