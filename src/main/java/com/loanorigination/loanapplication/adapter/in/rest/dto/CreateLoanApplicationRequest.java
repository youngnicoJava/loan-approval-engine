package com.loanorigination.loanapplication.adapter.in.rest.dto;

import com.loanorigination.loanapplication.application.command.CreateLoanApplicationCommand;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
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
        String purpose

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
                purpose
        );
    }
}