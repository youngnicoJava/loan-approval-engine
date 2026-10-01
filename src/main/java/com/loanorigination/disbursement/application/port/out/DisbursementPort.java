package com.loanorigination.disbursement.application.port.out;
import com.loanorigination.disbursement.domain.Disbursement;
import java.time.Instant;
public interface DisbursementPort { DisbursementResult execute(Disbursement disbursement); }
