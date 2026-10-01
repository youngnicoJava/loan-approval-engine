package com.loanorigination.loan.application.port.in;
import com.loanorigination.loan.domain.Loan;
import java.util.UUID;
public interface GetLoanUseCase { Loan get(UUID id); }
