package com.loanorigination.disbursement.application.exception;
import java.util.UUID;
public class LoanNotEligibleForDisbursementException extends RuntimeException { public LoanNotEligibleForDisbursementException(UUID loanId){super("Loan is not eligible for disbursement: "+loanId);} }
