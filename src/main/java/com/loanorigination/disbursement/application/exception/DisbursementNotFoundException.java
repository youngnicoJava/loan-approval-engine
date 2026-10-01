package com.loanorigination.disbursement.application.exception;
import java.util.UUID;
public class DisbursementNotFoundException extends RuntimeException { public DisbursementNotFoundException(UUID loanId){super("Disbursement not found for loan: "+loanId);} }
