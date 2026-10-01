package com.loanorigination.disbursement.domain;

import com.loanorigination.shared.domain.Money;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Registro del movimiento que materializa el préstamo; no reemplaza el estado del Loan. */
public final class Disbursement {
    private final UUID id,loanId;
    private final Money amount;
    private final Instant requestedAt;
    private DisbursementStatus status;
    private Instant processingAt,completedAt,failedAt,cancelledAt;
    private String externalReference,failureReason;
    private Disbursement(UUID id,UUID loanId,Money amount,DisbursementStatus status,Instant requestedAt,Instant processingAt,
            Instant completedAt,Instant failedAt,Instant cancelledAt,String reference,String failureReason){
        this.id=Objects.requireNonNull(id);this.loanId=Objects.requireNonNull(loanId);this.amount=Objects.requireNonNull(amount);
        if(amount.amount().signum()<=0)throw new IllegalArgumentException("Disbursement amount must be positive");
        this.status=Objects.requireNonNull(status);this.requestedAt=Objects.requireNonNull(requestedAt);this.processingAt=processingAt;
        this.completedAt=completedAt;this.failedAt=failedAt;this.cancelledAt=cancelledAt;this.externalReference=reference;this.failureReason=failureReason;
    }
    public static Disbursement create(UUID loanId,Money amount,Instant now){return new Disbursement(UUID.randomUUID(),loanId,amount,DisbursementStatus.PENDING,now,null,null,null,null,null,null);}
    public static Disbursement restore(UUID id,UUID loanId,Money amount,DisbursementStatus status,Instant requestedAt,Instant processingAt,Instant completedAt,Instant failedAt,Instant cancelledAt,String reference,String failureReason){return new Disbursement(id,loanId,amount,status,requestedAt,processingAt,completedAt,failedAt,cancelledAt,reference,failureReason);}
    public void beginProcessing(Instant now){if(status!=DisbursementStatus.PENDING&&status!=DisbursementStatus.FAILED)throw new IllegalStateException("Only pending or failed disbursements can be processed");status=DisbursementStatus.PROCESSING;processingAt=Objects.requireNonNull(now);failedAt=null;failureReason=null;}
    public void complete(Instant at,String reference){if(status!=DisbursementStatus.PROCESSING)throw new IllegalStateException("Only processing disbursements can complete");completedAt=Objects.requireNonNull(at);externalReference=requireText(reference,"externalReference");status=DisbursementStatus.COMPLETED;}
    public void fail(Instant at,String reason){if(status!=DisbursementStatus.PROCESSING)throw new IllegalStateException("Only processing disbursements can fail");failedAt=Objects.requireNonNull(at);failureReason=requireText(reason,"failureReason");status=DisbursementStatus.FAILED;}
    public void cancel(Instant at){if(status!=DisbursementStatus.PENDING&&status!=DisbursementStatus.FAILED)throw new IllegalStateException("Only pending or failed disbursements can be cancelled");cancelledAt=Objects.requireNonNull(at);status=DisbursementStatus.CANCELLED;}
    private static String requireText(String value,String name){Objects.requireNonNull(value,name);String v=value.trim();if(v.isBlank())throw new IllegalArgumentException(name+" cannot be blank");return v;}
    public UUID id(){return id;}public UUID loanId(){return loanId;}public Money amount(){return amount;}public DisbursementStatus status(){return status;}
    public Instant requestedAt(){return requestedAt;}public Instant processingAt(){return processingAt;}public Instant completedAt(){return completedAt;}
    public Instant failedAt(){return failedAt;}public Instant cancelledAt(){return cancelledAt;}public String externalReference(){return externalReference;}public String failureReason(){return failureReason;}
}
