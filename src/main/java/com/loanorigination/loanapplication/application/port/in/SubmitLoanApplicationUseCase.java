package com.loanorigination.loanapplication.application.port.in;

import com.loanorigination.loanapplication.domain.LoanApplication;
import java.util.UUID;

public interface SubmitLoanApplicationUseCase {

    LoanApplication submit(UUID loanApplicationId);
}