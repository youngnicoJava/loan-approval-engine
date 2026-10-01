package com.loanorigination.repayment.adapter.out.persistence;
import com.loanorigination.repayment.domain.*;
import com.loanorigination.shared.domain.Money;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;
@Entity @Table(name="installments") public class InstallmentEntity extends PanacheEntityBase{
    @Id public UUID id;
    @Column(name="schedule_id",nullable=false) public UUID scheduleId;
    @Column(name="loan_id",nullable=false) public UUID loanId;
    @Column(name="installment_number",nullable=false) public int installmentNumber;
    @Column(name="due_date",nullable=false) public LocalDate dueDate;
    @Column(name="principal_amount",nullable=false,precision=19,scale=2) public BigDecimal principalAmount;
    @Column(name="interest_amount",nullable=false,precision=19,scale=2) public BigDecimal interestAmount;
    @Column(name="total_amount",nullable=false,precision=19,scale=2) public BigDecimal totalAmount;
    @Column(name="remaining_principal",nullable=false,precision=19,scale=2) public BigDecimal remainingPrincipal;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public InstallmentStatus status;
    @Column(nullable=false,length=3) public String currency;
    protected InstallmentEntity(){}
    static InstallmentEntity fromDomain(UUID scheduleId,Installment i){var e=new InstallmentEntity();e.id=i.id();e.scheduleId=scheduleId;e.update(i);return e;}
    void update(Installment i){loanId=i.loanId();installmentNumber=i.installmentNumber();dueDate=i.dueDate();principalAmount=i.principalAmount().amount();interestAmount=i.interestAmount().amount();totalAmount=i.totalAmount().amount();remainingPrincipal=i.remainingPrincipal().amount();currency=i.totalAmount().currency().getCurrencyCode();status=i.status();}
    Installment toDomain(){var c=Currency.getInstance(currency);return Installment.restore(id,loanId,installmentNumber,dueDate,Money.of(principalAmount,c),Money.of(interestAmount,c),Money.of(totalAmount,c),Money.of(remainingPrincipal,c),status);}
}
