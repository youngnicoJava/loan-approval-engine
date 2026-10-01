package com.loanorigination.loanapplication.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.in.GetLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class GetLoanApplicationService implements GetLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    @Inject
    public GetLoanApplicationService(
            LoanApplicationRepository loanApplicationRepository
    ) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Override
    public LoanApplication get(UUID loanApplicationId) {

        return loanApplicationRepository
                .findById(loanApplicationId)
                .orElseThrow(() -> new LoanApplicationNotFoundException(loanApplicationId));
    }
}