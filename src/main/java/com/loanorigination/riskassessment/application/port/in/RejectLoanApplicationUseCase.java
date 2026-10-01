package com.loanorigination.riskassessment.application.port.in;

import com.loanorigination.loanapplication.domain.LoanApplication;

import java.util.UUID;

public interface RejectLoanApplicationUseCase {

    LoanApplication reject(UUID loanApplicationId);
}