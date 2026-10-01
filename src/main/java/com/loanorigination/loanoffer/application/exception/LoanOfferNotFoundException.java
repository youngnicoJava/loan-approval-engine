package com.loanorigination.loanoffer.application.exception;
import java.util.UUID;
public class LoanOfferNotFoundException extends RuntimeException { public LoanOfferNotFoundException(UUID id){super("Loan offer not found: "+id);} }
