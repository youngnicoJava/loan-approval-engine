package com.loanorigination.loanoffer.adapter.in.rest.dto;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.loanoffer.domain.LoanOfferStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record LoanOfferResponse(UUID id, UUID loanApplicationId, UUID customerId, BigDecimal principal,
        String currency, int termMonths, BigDecimal annualInterestRatePercentage,
        BigDecimal monthlyInstallment, BigDecimal totalRepayment, String installmentFrequency,
        LoanOfferStatus status, Instant createdAt, Instant expiresAt, Instant acceptedAt, Instant declinedAt) {
    public static LoanOfferResponse from(LoanOffer o){return new LoanOfferResponse(o.id(),o.loanApplicationId(),o.customerId(),o.principal().amount(),o.principal().currency().getCurrencyCode(),o.term().months(),o.annualInterestRate().percentage(),o.monthlyInstallment(),o.totalRepayment(),"MONTHLY",o.status(),o.createdAt(),o.expiresAt(),o.acceptedAt(),o.declinedAt());}
}
