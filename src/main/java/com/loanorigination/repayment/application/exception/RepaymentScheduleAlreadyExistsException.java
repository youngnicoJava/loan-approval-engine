package com.loanorigination.repayment.application.exception;
import java.util.UUID;
public class RepaymentScheduleAlreadyExistsException extends RuntimeException { public RepaymentScheduleAlreadyExistsException(UUID loanId){super("An active repayment schedule already exists for loan: "+loanId);} }
