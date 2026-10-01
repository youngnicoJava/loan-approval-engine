package com.loanorigination.loanoffer.application.exception;
import java.util.UUID;
public class LoanOfferAlreadyExistsException extends RuntimeException { public LoanOfferAlreadyExistsException(UUID id){super("A pending or accepted offer already exists for application: "+id);} }
