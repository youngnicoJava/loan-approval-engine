package com.loanorigination.repayment.adapter.out.persistence;
import com.loanorigination.repayment.domain.*;
import com.loanorigination.shared.domain.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;
@Entity @Table(name="repayment_schedules") public class RepaymentScheduleEntity extends PanacheEntityBase{
    @Id public UUID id;
    @Column(name="loan_id",nullable=false) public UUID loanId;
    @Enumerated(EnumType.STRING) @Column(name="amortization_type",nullable=false,length=40) public AmortizationType amortizationType;
    @Column(nullable=false,precision=19,scale=2) public BigDecimal principal;
    @Column(nullable=false,length=3) public String currency;
    @Column(name="annual_interest_rate_percentage",nullable=false,precision=9,scale=4) public BigDecimal annualRate;
    @Column(name="term_months",nullable=false) public int termMonths;
    @Column(name="disbursed_on",nullable=false) public LocalDate disbursedOn;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public RepaymentScheduleStatus status;
    @Column(name="created_at",nullable=false) public Instant createdAt;
    protected RepaymentScheduleEntity(){}
    static RepaymentScheduleEntity fromDomain(RepaymentSchedule s){var e=new RepaymentScheduleEntity();e.id=s.id();e.update(s);return e;}
    void update(RepaymentSchedule s){loanId=s.loanId();amortizationType=s.amortizationType();principal=s.principal().amount();currency=s.principal().currency().getCurrencyCode();annualRate=s.interestRate().percentage();termMonths=s.term().months();disbursedOn=s.disbursedOn();status=s.status();createdAt=s.createdAt();}
    RepaymentSchedule toDomain(java.util.List<Installment> installments){return RepaymentSchedule.restore(id,loanId,amortizationType,Money.of(principal,Currency.getInstance(currency)),InterestRate.ofPercentage(annualRate),LoanTerm.ofMonths(termMonths),disbursedOn,installments,createdAt,status);}
}
