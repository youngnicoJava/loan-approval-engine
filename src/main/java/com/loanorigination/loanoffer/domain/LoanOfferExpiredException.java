package com.loanorigination.loanoffer.domain;

import java.util.UUID;

public class LoanOfferExpiredException extends RuntimeException {
    public LoanOfferExpiredException(UUID id) { super("Loan offer has expired: " + id); }
}
