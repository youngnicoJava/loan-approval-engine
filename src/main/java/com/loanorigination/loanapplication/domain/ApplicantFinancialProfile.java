package com.loanorigination.loanapplication.domain;

import com.loanorigination.shared.domain.Money;
import java.util.Objects;

/** Información financiera declarada por el solicitante y utilizada por la evaluación de riesgo. */
public record ApplicantFinancialProfile(Money monthlyIncome, Money existingMonthlyDebtObligations,
        ApplicantEmploymentStatus employmentStatus, int employmentTenureMonths) {
    public ApplicantFinancialProfile {
        Objects.requireNonNull(monthlyIncome); Objects.requireNonNull(existingMonthlyDebtObligations); Objects.requireNonNull(employmentStatus);
        if (monthlyIncome.amount().signum() <= 0) throw new IllegalArgumentException("Monthly income must be greater than zero");
        if (!monthlyIncome.currency().equals(existingMonthlyDebtObligations.currency())) throw new IllegalArgumentException("Financial amounts must use the application currency");
        if (employmentTenureMonths < 0 || employmentTenureMonths > 600) throw new IllegalArgumentException("Employment tenure must be between 0 and 600 months");
    }
}
