package com.loanorigination.customer.domain;

/**
 * Representa una operación que intenta romper una transición
 * válida del ciclo de vida de un Customer.
 *
 * Las reglas de estado viven en el agregado y no en los adapters.
 */
public class InvalidCustomerStateException extends RuntimeException {

    public InvalidCustomerStateException(String message) {
        super(message);
    }
}