package com.loanorigination.shared.domain;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
/** Política compartida de precisión para cálculos financieros. */
public final class FinancialRounding {
    public static final MathContext CALCULATION_CONTEXT = new MathContext(34, RoundingMode.HALF_EVEN);
    public static final RoundingMode MONETARY_ROUNDING = RoundingMode.HALF_UP;
    private FinancialRounding() {}
    public static int scale(Currency currency) { return Math.max(0, currency.getDefaultFractionDigits()); }
}
