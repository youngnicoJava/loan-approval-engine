package com.loanorigination.loan.domain;

public class InvalidLoanStateException extends RuntimeException {
    public InvalidLoanStateException(String message) { super(message); }
}
