package com.loanorigination.loanoffer.application.exception;
import java.util.UUID;
public class LoanApplicationNotApprovedException extends RuntimeException { public LoanApplicationNotApprovedException(UUID id){super("Loan application must be approved before offer issuance: "+id);} }
