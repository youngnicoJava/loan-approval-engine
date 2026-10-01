package com.loanorigination.disbursement.application.port.out;
import java.time.Instant;
public record DisbursementResult(boolean completed,String externalReference,String failureReason,Instant completedAt){
    public static DisbursementResult completed(String reference,Instant at){return new DisbursementResult(true,reference,null,at);}
    public static DisbursementResult failed(String reason){return new DisbursementResult(false,null,reason,null);}
}
