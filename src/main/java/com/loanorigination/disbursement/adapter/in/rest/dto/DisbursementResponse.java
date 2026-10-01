package com.loanorigination.disbursement.adapter.in.rest.dto;
import com.loanorigination.disbursement.domain.Disbursement;
import com.loanorigination.disbursement.domain.DisbursementStatus;
import java.time.Instant;
import java.util.UUID;
public record DisbursementResponse(UUID id,UUID loanId,java.math.BigDecimal amount,String currency,DisbursementStatus status,
        Instant requestedAt,Instant processingAt,Instant completedAt,Instant failedAt,Instant cancelledAt,
        String externalReference,String failureReason){
    public static DisbursementResponse from(Disbursement d){return new DisbursementResponse(d.id(),d.loanId(),d.amount().amount(),d.amount().currency().getCurrencyCode(),d.status(),d.requestedAt(),d.processingAt(),d.completedAt(),d.failedAt(),d.cancelledAt(),d.externalReference(),d.failureReason());}
}
