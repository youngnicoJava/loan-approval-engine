package com.loanorigination.shared.domain;

import java.util.Objects;

/**
 * Representa el plazo de un préstamo en meses.
 *
 * El objeto valida solamente la regla universal de que un plazo
 * debe ser positivo. Los límites específicos de cada producto
 * pertenecen a las políticas del producto y no a este Value Object.
 */
public record LoanTerm(
        int months
) {

    public LoanTerm {
        if (months <= 0) {
            throw new IllegalArgumentException("Loan term must be greater than zero");
        }
    }

    public static LoanTerm ofMonths(int months) {
        return new LoanTerm(months);
    }
}