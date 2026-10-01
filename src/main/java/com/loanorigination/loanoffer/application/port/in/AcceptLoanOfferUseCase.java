package com.loanorigination.loanoffer.application.port.in;
import com.loanorigination.loan.domain.Loan;
import java.util.UUID;
public interface AcceptLoanOfferUseCase { Loan accept(UUID offerId); }
