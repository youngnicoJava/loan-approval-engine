package com.loanorigination.repayment.application.exception;
import java.util.UUID;
public class RepaymentScheduleNotFoundException extends RuntimeException { public RepaymentScheduleNotFoundException(UUID loanId){super("Repayment schedule not found for loan: "+loanId);} }
