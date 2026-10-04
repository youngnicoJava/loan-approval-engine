package com.loanorigination.loan.application.service;

import com.loanorigination.loan.application.port.in.ListOperationalLoansUseCase;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.Loan;
import com.loanorigination.loan.domain.LoanStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListOperationalLoansService implements ListOperationalLoansUseCase {
    private final LoanRepository loans;

    @Inject
    public ListOperationalLoansService(LoanRepository loans) {
        this.loans = loans;
    }

    @Override
    public List<Loan> list(LoanStatus status, int limit) {
        return loans.findForOperations(status, limit);
    }
}
