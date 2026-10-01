package com.loanorigination.repayment.application.port.out;
import com.loanorigination.loan.domain.Loan;
import com.loanorigination.repayment.domain.AmortizationQuote;
import com.loanorigination.repayment.domain.RepaymentSchedule;
import com.loanorigination.shared.domain.InterestRate;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import java.time.Instant;
public interface AmortizationCalculatorPort {
    AmortizationQuote calculateQuote(Money principal,InterestRate annualRate,LoanTerm term);
    RepaymentSchedule generateSchedule(Loan loan,Instant disbursedAt,Instant createdAt);
}
