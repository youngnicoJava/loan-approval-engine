package com.loanorigination.loanoffer.domain;

public class InvalidLoanOfferStateException extends RuntimeException {
    public InvalidLoanOfferStateException(String message) { super(message); }
}
