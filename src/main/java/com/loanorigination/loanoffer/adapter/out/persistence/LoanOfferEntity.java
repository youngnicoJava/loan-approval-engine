package com.loanorigination.loanoffer.adapter.out.persistence;
import com.loanorigination.loanoffer.domain.*;
import com.loanorigination.shared.domain.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;
@Entity @Table(name="loan_offers") public class LoanOfferEntity extends PanacheEntityBase {
    @Id public UUID id;
    @Column(name="loan_application_id",nullable=false) public UUID loanApplicationId;
    @Column(name="customer_id",nullable=false) public UUID customerId;
    @Column(nullable=false,precision=19,scale=2) public BigDecimal principal;
    @Column(nullable=false,length=3) public String currency;
    @Column(name="term_months",nullable=false) public int termMonths;
    @Column(name="annual_interest_rate_percentage",nullable=false,precision=9,scale=4) public BigDecimal annualRate;
    @Column(name="monthly_installment",nullable=false,precision=19,scale=2) public BigDecimal monthlyInstallment;
    @Column(name="total_repayment",nullable=false,precision=19,scale=2) public BigDecimal totalRepayment;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public LoanOfferStatus status;
    @Column(name="created_at",nullable=false) public Instant createdAt;
    @Column(name="expires_at",nullable=false) public Instant expiresAt;
    @Column(name="accepted_at") public Instant acceptedAt;
    @Column(name="declined_at") public Instant declinedAt;
    protected LoanOfferEntity(){}
    static LoanOfferEntity fromDomain(LoanOffer o){LoanOfferEntity e=new LoanOfferEntity();e.id=o.id();e.update(o);return e;}
    void update(LoanOffer o){loanApplicationId=o.loanApplicationId();customerId=o.customerId();principal=o.principal().amount();currency=o.principal().currency().getCurrencyCode();termMonths=o.term().months();annualRate=o.annualInterestRate().percentage();monthlyInstallment=o.monthlyInstallment();totalRepayment=o.totalRepayment();status=o.status();createdAt=o.createdAt();expiresAt=o.expiresAt();acceptedAt=o.acceptedAt();declinedAt=o.declinedAt();}
    LoanOffer toDomain(){return LoanOffer.restore(id,loanApplicationId,customerId,Money.of(principal,Currency.getInstance(currency)),LoanTerm.ofMonths(termMonths),InterestRate.ofPercentage(annualRate),monthlyInstallment,totalRepayment,status,createdAt,expiresAt,acceptedAt,declinedAt);}
}
