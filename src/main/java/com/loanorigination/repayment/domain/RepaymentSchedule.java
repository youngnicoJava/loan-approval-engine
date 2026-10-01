package com.loanorigination.repayment.domain;

import com.loanorigination.shared.domain.InterestRate;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Aggregate que protege la consistencia del plan contractual de cuotas. */
public final class RepaymentSchedule {
    private final UUID id, loanId;
    private final AmortizationType amortizationType;
    private final Money principal;
    private final InterestRate interestRate;
    private final LoanTerm term;
    private final LocalDate disbursedOn;
    private final List<Installment> installments;
    private final Instant createdAt;
    private RepaymentScheduleStatus status;

    private RepaymentSchedule(UUID id,UUID loanId,AmortizationType type,Money principal,InterestRate rate,LoanTerm term,
            LocalDate disbursedOn,List<Installment> installments,Instant createdAt,RepaymentScheduleStatus status){
        this.id=Objects.requireNonNull(id);this.loanId=Objects.requireNonNull(loanId);this.amortizationType=Objects.requireNonNull(type);
        this.principal=Objects.requireNonNull(principal);if(principal.amount().signum()<=0)throw new IllegalArgumentException("principal must be positive");
        this.interestRate=Objects.requireNonNull(rate);this.term=Objects.requireNonNull(term);this.disbursedOn=Objects.requireNonNull(disbursedOn);
        this.installments=List.copyOf(Objects.requireNonNull(installments));this.createdAt=Objects.requireNonNull(createdAt);this.status=Objects.requireNonNull(status);
        validateInstallments();
    }
    public static RepaymentSchedule create(UUID loanId,AmortizationType type,Money principal,InterestRate rate,LoanTerm term,
            LocalDate disbursedOn,List<Installment> installments,Instant createdAt){
        return new RepaymentSchedule(UUID.randomUUID(),loanId,type,principal,rate,term,disbursedOn,installments,createdAt,RepaymentScheduleStatus.ACTIVE);
    }
    public static RepaymentSchedule restore(UUID id,UUID loanId,AmortizationType type,Money principal,InterestRate rate,LoanTerm term,
            LocalDate disbursedOn,List<Installment> installments,Instant createdAt,RepaymentScheduleStatus status){
        return new RepaymentSchedule(id,loanId,type,principal,rate,term,disbursedOn,installments,createdAt,status);
    }
    private void validateInstallments(){
        if(installments.size()!=term.months())throw new IllegalArgumentException("Installment count must equal loan term");
        BigDecimal principalSum=BigDecimal.ZERO;LocalDate previous=disbursedOn;BigDecimal expectedRemaining=principal.amount();
        for(int i=0;i<installments.size();i++){
            Installment item=installments.get(i);
            if(!item.loanId().equals(loanId)||item.installmentNumber()!=i+1)throw new IllegalArgumentException("Installments must belong to the loan and be sequential starting at 1");
            if(!item.dueDate().isAfter(previous))throw new IllegalArgumentException("Installment due dates must advance");
            if(!item.principalAmount().currency().equals(principal.currency()))throw new IllegalArgumentException("Schedule currency mismatch");
            principalSum=principalSum.add(item.principalAmount().amount());expectedRemaining=expectedRemaining.subtract(item.principalAmount().amount());
            if(expectedRemaining.compareTo(item.remainingPrincipal().amount())!=0)throw new IllegalArgumentException("Remaining principal does not match amortization balance");
            previous=item.dueDate();
        }
        if(principalSum.compareTo(principal.amount())!=0||expectedRemaining.signum()!=0)throw new IllegalArgumentException("Installment principal must amortize the original principal exactly");
    }
    public void cancel(){if(status!=RepaymentScheduleStatus.ACTIVE)throw new IllegalStateException("Only an active schedule can be cancelled");if(installments.stream().anyMatch(i->i.status()==InstallmentStatus.PAID))throw new IllegalStateException("A schedule with paid installments cannot be cancelled");for(var item:installments)item.cancel();status=RepaymentScheduleStatus.CANCELLED;}
    public UUID id(){return id;}public UUID loanId(){return loanId;}public AmortizationType amortizationType(){return amortizationType;}
    public Money principal(){return principal;}public InterestRate interestRate(){return interestRate;}public LoanTerm term(){return term;}
    public LocalDate disbursedOn(){return disbursedOn;}public List<Installment> installments(){return installments;}public Instant createdAt(){return createdAt;}
    public RepaymentScheduleStatus status(){return status;}
}
