package com.loanorigination.loanoffer.application.exception;
import java.util.UUID;
public class LoanOfferAccessDeniedException extends RuntimeException { public LoanOfferAccessDeniedException(UUID id){super("Access denied for loan offer: "+id);} }
