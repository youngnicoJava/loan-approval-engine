package com.loanorigination.loanapplication.application.port.in;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import java.util.List;

public interface ListLoanApplicationsUseCase {
    List<LoanApplication> forCurrentCustomer();
    List<LoanApplication> forOperations(LoanApplicationStatus status, int offset, int limit);
}
