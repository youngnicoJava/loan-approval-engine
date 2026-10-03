package com.loanorigination.loanapplication.adapter.in.rest.dto;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.loanapplication.domain.LoanProductType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanApplicationResponse(
        UUID id,
        UUID customerId,
        LoanProductType productType,
        BigDecimal requestedAmount,
        String currency,
        int termMonths,
        String purpose,
        java.math.BigDecimal monthlyIncome,
        java.math.BigDecimal existingMonthlyDebtObligations,
        String employmentStatus,
        Integer employmentTenureMonths,
        LoanApplicationStatus status,
        Instant createdAt,
        Instant submittedAt,
        Instant updatedAt
) {

    public static LoanApplicationResponse from(
            LoanApplication loanApplication
    ) {
        return new LoanApplicationResponse(
                loanApplication.id(),
                loanApplication.customerId(),
                loanApplication.productType(),
                loanApplication.requestedAmount().amount(),
                loanApplication.requestedAmount().currency().getCurrencyCode(),
                loanApplication.term().months(),
                loanApplication.purpose(),
                loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().monthlyIncome().amount(),
                loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().existingMonthlyDebtObligations().amount(),
                loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().employmentStatus().name(),
                loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().employmentTenureMonths(),
                loanApplication.status(),
                loanApplication.createdAt(),
                loanApplication.submittedAt(),
                loanApplication.updatedAt()
        );
    }
}
