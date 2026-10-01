package com.loanorigination.repayment.domain;
import java.math.BigDecimal;
import java.util.Objects;
public record AmortizationQuote(BigDecimal regularInstallment,BigDecimal totalRepayment){
    public AmortizationQuote{Objects.requireNonNull(regularInstallment);Objects.requireNonNull(totalRepayment);if(regularInstallment.signum()<=0||totalRepayment.signum()<=0)throw new IllegalArgumentException("Quote amounts must be positive");}
}
