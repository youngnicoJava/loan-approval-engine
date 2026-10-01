package com.loanorigination.repayment.domain;

import com.loanorigination.shared.domain.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** Una obligación individual del calendario de amortización. */
public final class Installment {
    private final UUID id, loanId;
    private final int installmentNumber;
    private final LocalDate dueDate;
    private final Money principalAmount, interestAmount, totalAmount, remainingPrincipal;
    private InstallmentStatus status;

    private Installment(UUID id, UUID loanId, int number, LocalDate dueDate, Money principal,
            Money interest, Money total, Money remaining, InstallmentStatus status) {
        this.id=Objects.requireNonNull(id); this.loanId=Objects.requireNonNull(loanId);
        if(number<1) throw new IllegalArgumentException("installmentNumber must start at 1");
        this.installmentNumber=number; this.dueDate=Objects.requireNonNull(dueDate);
        this.principalAmount=nonNegative(principal,"principalAmount"); this.interestAmount=nonNegative(interest,"interestAmount");
        this.totalAmount=nonNegative(total,"totalAmount"); this.remainingPrincipal=nonNegative(remaining,"remainingPrincipal");
        sameCurrency(this.principalAmount,this.interestAmount,this.totalAmount,this.remainingPrincipal);
        if(this.principalAmount.amount().add(this.interestAmount.amount()).compareTo(this.totalAmount.amount())!=0)
            throw new IllegalArgumentException("totalAmount must equal principalAmount plus interestAmount");
        this.status=Objects.requireNonNull(status);
    }
    public static Installment create(UUID loanId,int number,LocalDate dueDate,Money principal,Money interest,Money total,Money remaining){
        return new Installment(UUID.randomUUID(),loanId,number,dueDate,principal,interest,total,remaining,InstallmentStatus.PENDING);
    }
    public static Installment restore(UUID id,UUID loanId,int number,LocalDate dueDate,Money principal,Money interest,Money total,Money remaining,InstallmentStatus status){
        return new Installment(id,loanId,number,dueDate,principal,interest,total,remaining,status);
    }
    public void markPaid(){if(status!=InstallmentStatus.PENDING&&status!=InstallmentStatus.OVERDUE)throw new IllegalStateException("Only pending or overdue installments can be paid");status=InstallmentStatus.PAID;}
    public void markOverdue(LocalDate today){if(status==InstallmentStatus.PENDING&&dueDate.isBefore(Objects.requireNonNull(today)))status=InstallmentStatus.OVERDUE;}
    public void cancel(){if(status==InstallmentStatus.PAID)throw new IllegalStateException("Paid installments cannot be cancelled");status=InstallmentStatus.CANCELLED;}
    private static Money nonNegative(Money m,String n){Objects.requireNonNull(m,n);if(m.amount().signum()<0)throw new IllegalArgumentException(n+" cannot be negative");return m;}
    private static void sameCurrency(Money... amounts){var c=amounts[0].currency();for(var m:amounts)if(!c.equals(m.currency()))throw new IllegalArgumentException("Installment amounts must use the same currency");}
    public UUID id(){return id;} public UUID loanId(){return loanId;} public int installmentNumber(){return installmentNumber;}
    public LocalDate dueDate(){return dueDate;} public Money principalAmount(){return principalAmount;} public Money interestAmount(){return interestAmount;}
    public Money totalAmount(){return totalAmount;} public Money remainingPrincipal(){return remainingPrincipal;} public InstallmentStatus status(){return status;}
}
