package com.loanorigination.loanapplication.application.exception;

import java.util.UUID;

public class LoanApplicationAccessDeniedException extends RuntimeException {

    public LoanApplicationAccessDeniedException(UUID loanApplicationId) {
        super("Access denied for loan application: " + loanApplicationId);
    }
}