package com.loanorigination.loanapplication.adapter.in.rest.dto;

import com.loanorigination.loanapplication.application.command.CreateLoanApplicationCommand;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import com.loanorigination.loanapplication.domain.ApplicantEmploymentStatus;
import com.loanorigination.loanapplication.domain.ApplicantFinancialProfile;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

public record CreateLoanApplicationRequest(

        @NotNull
        LoanProductType productType,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal requestedAmount,

        @NotBlank
        @Pattern(regexp = "[A-Za-z]{3}")
        String currency,

        @Min(1)
        int termMonths,

        @NotBlank
        @Size(max = 500)
        String purpose,

        @NotNull @DecimalMin("0.01") @jakarta.validation.constraints.Digits(integer = 17, fraction = 2)
        BigDecimal monthlyIncome,

        @NotNull @DecimalMin("0.00") @jakarta.validation.constraints.Digits(integer = 17, fraction = 2)
        BigDecimal existingMonthlyDebtObligations,

        @NotNull
        ApplicantEmploymentStatus employmentStatus,

        @Min(0) @jakarta.validation.constraints.Max(600)
        int employmentTenureMonths

) {

    public CreateLoanApplicationCommand toCommand() {

        String normalizedCurrency = currency
                .trim()
                .toUpperCase(Locale.ROOT);

        return new CreateLoanApplicationCommand(
                productType,
                Money.of(
                        requestedAmount,
                        Currency.getInstance(normalizedCurrency)
                ),
                LoanTerm.ofMonths(termMonths),
                purpose,
                new ApplicantFinancialProfile(
                        Money.of(monthlyIncome, Currency.getInstance(normalizedCurrency)),
                        Money.of(existingMonthlyDebtObligations, Currency.getInstance(normalizedCurrency)),
                        employmentStatus,
                        employmentTenureMonths
                )
        );
    }
}
