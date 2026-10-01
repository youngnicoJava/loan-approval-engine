package com.loanorigination.shared.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * Representa una tasa de interés expresada como porcentaje.
 *
 * Ejemplo:
 * 12.50 representa una tasa del 12,50%.
 *
 * Se mantiene separada de BigDecimal para que el dominio no tenga
 * que depender de convenciones implícitas sobre qué significa ese número.
 */
public record InterestRate(
        BigDecimal percentage
) {

    public InterestRate {
        Objects.requireNonNull(percentage, "percentage cannot be null");

        if (percentage.signum() < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
    }

    public static InterestRate ofPercentage(BigDecimal percentage) {
        return new InterestRate(percentage);
    }

    /** Convierte una tasa porcentual nominal anual a tasa periódica mensual. */
    public BigDecimal monthlyPeriodicRate(MathContext mathContext) {
        Objects.requireNonNull(mathContext, "mathContext cannot be null");
        return percentage.divide(BigDecimal.valueOf(1200), mathContext);
    }
}
