package com.loanorigination.loanapplication.domain;

/**
 * Se utiliza cuando una operación intenta romper una transición
 * válida del ciclo de vida de una solicitud.
 *
 * Esto permite distinguir un error de negocio de un error técnico.
 */
public class InvalidLoanApplicationStateException extends RuntimeException {

    public InvalidLoanApplicationStateException(String message) {
        super(message);
    }
}