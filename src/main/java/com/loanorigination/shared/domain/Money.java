package com.loanorigination.shared.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Representa una cantidad monetaria junto con su moneda.
 *
 * La aplicación no debería transportar dinero como un BigDecimal aislado,
 * porque el valor sin moneda no representa correctamente un concepto monetario.
 *
 * El redondeo y las operaciones financieras más específicas se incorporarán
 * en los casos de uso que realmente las necesiten; este objeto mantiene
 * por ahora la representación monetaria básica e inequívoca.
 */
public record Money(
        BigDecimal amount,
        Currency currency
) {

    public Money {
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }
}