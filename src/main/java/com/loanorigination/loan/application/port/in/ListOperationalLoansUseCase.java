package com.loanorigination.loan.application.port.in;

import com.loanorigination.loan.domain.Loan;
import com.loanorigination.loan.domain.LoanStatus;
import java.util.List;

public interface ListOperationalLoansUseCase {
    List<Loan> list(LoanStatus status, int limit);
}
