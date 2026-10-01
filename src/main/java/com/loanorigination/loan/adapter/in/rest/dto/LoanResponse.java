package com.loanorigination.loan.adapter.in.rest.dto;
import com.loanorigination.loan.domain.Loan;
import com.loanorigination.loan.domain.LoanStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record LoanResponse(UUID id, UUID loanOfferId, UUID loanApplicationId, UUID customerId,
        BigDecimal principal, String currency, int termMonths, BigDecimal annualInterestRatePercentage,
        BigDecimal monthlyInstallment, BigDecimal totalRepayment, LoanStatus status,
        Instant createdAt, Instant activatedAt, Instant paidOffAt) {
    public static LoanResponse from(Loan l){return new LoanResponse(l.id(),l.loanOfferId(),l.loanApplicationId(),l.customerId(),l.principal().amount(),l.principal().currency().getCurrencyCode(),l.term().months(),l.annualInterestRate().percentage(),l.monthlyInstallment(),l.totalRepayment(),l.status(),l.createdAt(),l.activatedAt(),l.paidOffAt());}
}
