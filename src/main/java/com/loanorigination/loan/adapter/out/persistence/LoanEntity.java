package com.loanorigination.loan.adapter.out.persistence;
import com.loanorigination.loan.domain.*;
import com.loanorigination.shared.domain.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;
@Entity @Table(name="loans") public class LoanEntity extends PanacheEntityBase {
    @Id public UUID id;
    @Column(name="loan_offer_id",nullable=false,unique=true) public UUID loanOfferId;
    @Column(name="loan_application_id",nullable=false,unique=true) public UUID loanApplicationId;
    @Column(name="customer_id",nullable=false) public UUID customerId;
    @Column(nullable=false,precision=19,scale=2) public BigDecimal principal;
    @Column(nullable=false,length=3) public String currency;
    @Column(name="term_months",nullable=false) public int termMonths;
    @Column(name="annual_interest_rate_percentage",nullable=false,precision=9,scale=4) public BigDecimal annualRate;
    @Column(name="monthly_installment",nullable=false,precision=19,scale=2) public BigDecimal monthlyInstallment;
    @Column(name="total_repayment",nullable=false,precision=19,scale=2) public BigDecimal totalRepayment;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) public LoanStatus status;
    @Column(name="created_at",nullable=false) public Instant createdAt;
    @Column(name="activated_at") public Instant activatedAt;
    @Column(name="paid_off_at") public Instant paidOffAt;
    protected LoanEntity(){}
    static LoanEntity fromDomain(Loan l){LoanEntity e=new LoanEntity();e.id=l.id();e.update(l);return e;}
    void update(Loan l){loanOfferId=l.loanOfferId();loanApplicationId=l.loanApplicationId();customerId=l.customerId();principal=l.principal().amount();currency=l.principal().currency().getCurrencyCode();termMonths=l.term().months();annualRate=l.annualInterestRate().percentage();monthlyInstallment=l.monthlyInstallment();totalRepayment=l.totalRepayment();status=l.status();createdAt=l.createdAt();activatedAt=l.activatedAt();paidOffAt=l.paidOffAt();}
    Loan toDomain(){return Loan.restore(id,loanOfferId,loanApplicationId,customerId,Money.of(principal,Currency.getInstance(currency)),LoanTerm.ofMonths(termMonths),InterestRate.ofPercentage(annualRate),monthlyInstallment,totalRepayment,status,createdAt,activatedAt,paidOffAt);}
}
