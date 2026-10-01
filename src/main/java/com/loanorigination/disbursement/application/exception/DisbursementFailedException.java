package com.loanorigination.disbursement.application.exception;
import java.util.UUID;
public class DisbursementFailedException extends RuntimeException { public DisbursementFailedException(UUID loanId,String reason){super("Disbursement failed for loan "+loanId+": "+reason);} }
