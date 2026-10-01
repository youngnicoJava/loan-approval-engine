package com.loanorigination.disbursement.adapter.out.persistence;
import com.loanorigination.disbursement.domain.*;
import com.loanorigination.shared.domain.Money;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;
@Entity @Table(name="disbursements") public class DisbursementEntity extends PanacheEntityBase {
    @Id public UUID id;
    @Column(name="loan_id",nullable=false,unique=true) public UUID loanId;
    @Column(nullable=false,precision=19,scale=2) public BigDecimal amount;
    @Column(nullable=false,length=3) public String currency;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public DisbursementStatus status;
    @Column(name="requested_at",nullable=false) public Instant requestedAt;
    @Column(name="processing_at") public Instant processingAt;
    @Column(name="completed_at") public Instant completedAt;
    @Column(name="failed_at") public Instant failedAt;
    @Column(name="cancelled_at") public Instant cancelledAt;
    @Column(name="external_reference",length=255) public String externalReference;
    @Column(name="failure_reason",length=1000) public String failureReason;
    protected DisbursementEntity(){}
    static DisbursementEntity fromDomain(Disbursement d){var e=new DisbursementEntity();e.id=d.id();e.update(d);return e;}
    void update(Disbursement d){loanId=d.loanId();amount=d.amount().amount();currency=d.amount().currency().getCurrencyCode();status=d.status();requestedAt=d.requestedAt();processingAt=d.processingAt();completedAt=d.completedAt();failedAt=d.failedAt();cancelledAt=d.cancelledAt();externalReference=d.externalReference();failureReason=d.failureReason();}
    Disbursement toDomain(){return Disbursement.restore(id,loanId,Money.of(amount,Currency.getInstance(currency)),status,requestedAt,processingAt,completedAt,failedAt,cancelledAt,externalReference,failureReason);}
}
