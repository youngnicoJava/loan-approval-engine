package com.loanorigination.loanapplication.application.command;

import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;

import java.util.Objects;
import java.util.UUID;

/**
 * Comando de aplicación para crear una solicitud.
 *
 * El comando expresa los datos necesarios para ejecutar el caso de uso,
 * pero no contiene lógica de negocio. Las invariantes pertenecen al dominio.
 */
public record CreateLoanApplicationCommand(
        UUID customerId,
        LoanProductType productType,
        Money requestedAmount,
        LoanTerm term,
        String purpose
) {

    public CreateLoanApplicationCommand {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(productType, "productType cannot be null");
        Objects.requireNonNull(requestedAmount, "requestedAmount cannot be null");
        Objects.requireNonNull(term, "term cannot be null");
        Objects.requireNonNull(purpose, "purpose cannot be null");
    }
}