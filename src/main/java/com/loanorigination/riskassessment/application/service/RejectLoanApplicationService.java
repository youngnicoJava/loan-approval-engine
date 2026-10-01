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

@ApplicationScoped
public class RejectLoanApplicationService implements RejectLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    @Inject
    public RejectLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Override
    @Transactional
    public LoanApplication reject(
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

        loanApplication.reject(
                Instant.now()
        );

        loanApplicationRepository.save(
                loanApplication
        );

        return loanApplication;
    }
}