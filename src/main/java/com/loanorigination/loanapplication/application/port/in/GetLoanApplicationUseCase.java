package com.loanorigination.loanapplication.application.port.in;

import com.loanorigination.loanapplication.domain.LoanApplication;

import java.util.UUID;

public interface GetLoanApplicationUseCase {

    LoanApplication get(UUID loanApplicationId);
}