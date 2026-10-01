package com.loanorigination.loan.application.exception;
import java.util.UUID;
public class LoanAccessDeniedException extends RuntimeException { public LoanAccessDeniedException(UUID id){super("Access denied for loan: "+id);} }
