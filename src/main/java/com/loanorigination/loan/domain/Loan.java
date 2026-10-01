package com.loanorigination.loan.domain;

import com.loanorigination.shared.domain.InterestRate;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Contract created from an accepted offer; disbursement is a later workflow. */
public final class Loan {
    private final UUID id, loanOfferId, loanApplicationId, customerId;
    private final Money principal;
    private final LoanTerm term;
    private final InterestRate annualInterestRate;
    private final BigDecimal monthlyInstallment, totalRepayment;
    private final Instant createdAt;
    private LoanStatus status;
    private Instant activatedAt, paidOffAt;

    private Loan(UUID id, UUID offerId, UUID applicationId, UUID customerId, Money principal,
            LoanTerm term, InterestRate rate, BigDecimal installment, BigDecimal total,
            LoanStatus status, Instant createdAt, Instant activatedAt, Instant paidOffAt) {
        this.id=Objects.requireNonNull(id); this.loanOfferId=Objects.requireNonNull(offerId);
        this.loanApplicationId=Objects.requireNonNull(applicationId); this.customerId=Objects.requireNonNull(customerId);
        this.principal=Objects.requireNonNull(principal); this.term=Objects.requireNonNull(term);
        this.annualInterestRate=Objects.requireNonNull(rate); this.monthlyInstallment=positive(installment);
        this.totalRepayment=positive(total); this.status=Objects.requireNonNull(status);
        this.createdAt=Objects.requireNonNull(createdAt); this.activatedAt=activatedAt; this.paidOffAt=paidOffAt;
    }
    public static Loan createFromAcceptedOffer(UUID offerId, UUID applicationId, UUID customerId,
            Money principal, LoanTerm term, InterestRate rate, BigDecimal installment,
            BigDecimal total, Instant now) {
        return new Loan(UUID.randomUUID(), offerId, applicationId, customerId, principal, term,
                rate, installment, total, LoanStatus.PENDING_DISBURSEMENT, now, null, null);
    }
    public static Loan restore(UUID id, UUID offerId, UUID applicationId, UUID customerId,
            Money principal, LoanTerm term, InterestRate rate, BigDecimal installment,
            BigDecimal total, LoanStatus status, Instant createdAt, Instant activatedAt, Instant paidOffAt) {
        return new Loan(id, offerId, applicationId, customerId, principal, term, rate,
                installment, total, status, createdAt, activatedAt, paidOffAt);
    }
    /** Called by the future disbursement use case after a confirmed transfer. */
    public void activate(Instant now) {
        if (status != LoanStatus.PENDING_DISBURSEMENT) throw new InvalidLoanStateException("Only pending-disbursement loans can be activated");
        status=LoanStatus.ACTIVE; activatedAt=Objects.requireNonNull(now);
    }
    public void payOff(Instant now) {
        if (status != LoanStatus.ACTIVE) throw new InvalidLoanStateException("Only active loans can be paid off");
        status=LoanStatus.PAID_OFF; paidOffAt=Objects.requireNonNull(now);
    }
    private static BigDecimal positive(BigDecimal value) {
        Objects.requireNonNull(value); if (value.signum()<=0) throw new IllegalArgumentException("Amount must be positive"); return value;
    }
    public UUID id(){return id;} public UUID loanOfferId(){return loanOfferId;} public UUID loanApplicationId(){return loanApplicationId;}
    public UUID customerId(){return customerId;} public Money principal(){return principal;} public LoanTerm term(){return term;}
    public InterestRate annualInterestRate(){return annualInterestRate;} public BigDecimal monthlyInstallment(){return monthlyInstallment;}
    public BigDecimal totalRepayment(){return totalRepayment;} public LoanStatus status(){return status;} public Instant createdAt(){return createdAt;}
    public Instant activatedAt(){return activatedAt;} public Instant paidOffAt(){return paidOffAt;}
}
