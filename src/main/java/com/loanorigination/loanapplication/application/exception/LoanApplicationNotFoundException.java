package com.loanorigination.loanapplication.application.exception;

import java.util.UUID;

public class LoanApplicationNotFoundException extends RuntimeException {

    public LoanApplicationNotFoundException(UUID loanApplicationId) {
        super("Loan application not found: " + loanApplicationId);
    }
}