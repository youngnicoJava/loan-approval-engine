package com.loanorigination.disbursement.application.port.in;
import com.loanorigination.disbursement.domain.Disbursement;
import java.util.UUID;
public interface DisburseLoanUseCase { Disbursement disburse(UUID loanId); }
