package com.loanorigination.loanapplication.application.port.in;

import com.loanorigination.loanapplication.application.command.CreateLoanApplicationCommand;
import com.loanorigination.loanapplication.domain.LoanApplication;

public interface CreateLoanApplicationUseCase {

    LoanApplication create(CreateLoanApplicationCommand command);
}