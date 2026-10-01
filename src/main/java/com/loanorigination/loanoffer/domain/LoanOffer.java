package com.loanorigination.loanoffer.domain;

import com.loanorigination.shared.domain.InterestRate;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Immutable offer terms with a guarded lifecycle. */
public final class LoanOffer {
    private final UUID id;
    private final UUID loanApplicationId;
    private final UUID customerId;
    private final Money principal;
    private final LoanTerm term;
    private final InterestRate annualInterestRate;
    private final BigDecimal monthlyInstallment;
    private final BigDecimal totalRepayment;
    private final Instant createdAt;
    private final Instant expiresAt;
    private LoanOfferStatus status;
    private Instant acceptedAt;
    private Instant declinedAt;

    private LoanOffer(UUID id, UUID loanApplicationId, UUID customerId, Money principal,
            LoanTerm term, InterestRate annualInterestRate, BigDecimal monthlyInstallment,
            BigDecimal totalRepayment, LoanOfferStatus status, Instant createdAt,
            Instant expiresAt, Instant acceptedAt, Instant declinedAt) {
        this.id = Objects.requireNonNull(id);
        this.loanApplicationId = Objects.requireNonNull(loanApplicationId);
        this.customerId = Objects.requireNonNull(customerId);
        this.principal = Objects.requireNonNull(principal);
        this.term = Objects.requireNonNull(term);
        this.annualInterestRate = Objects.requireNonNull(annualInterestRate);
        this.monthlyInstallment = positive(monthlyInstallment, "monthlyInstallment");
        this.totalRepayment = positive(totalRepayment, "totalRepayment");
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        if (!expiresAt.isAfter(createdAt)) throw new IllegalArgumentException("expiresAt must be after createdAt");
        this.acceptedAt = acceptedAt;
        this.declinedAt = declinedAt;
    }

    public static LoanOffer create(UUID applicationId, UUID customerId, Money principal,
            LoanTerm term, InterestRate annualRate, BigDecimal installment,
            BigDecimal total, Instant now, Instant expiresAt) {
        return new LoanOffer(UUID.randomUUID(), applicationId, customerId, principal, term,
                annualRate, installment, total, LoanOfferStatus.PENDING, now, expiresAt, null, null);
    }

    public static LoanOffer restore(UUID id, UUID applicationId, UUID customerId, Money principal,
            LoanTerm term, InterestRate annualRate, BigDecimal installment, BigDecimal total,
            LoanOfferStatus status, Instant createdAt, Instant expiresAt, Instant acceptedAt, Instant declinedAt) {
        return new LoanOffer(id, applicationId, customerId, principal, term, annualRate,
                installment, total, status, createdAt, expiresAt, acceptedAt, declinedAt);
    }

    public void accept(Instant now) {
        ensurePending(now);
        if (!now.isBefore(expiresAt)) {
            status = LoanOfferStatus.EXPIRED;
            throw new LoanOfferExpiredException(id);
        }
        status = LoanOfferStatus.ACCEPTED;
        acceptedAt = now;
    }

    public void decline(Instant now) {
        ensurePending(now);
        if (!now.isBefore(expiresAt)) {
            status = LoanOfferStatus.EXPIRED;
            declinedAt = null;
            return;
        }
        status = LoanOfferStatus.DECLINED;
        declinedAt = now;
    }

    public boolean expireIfDue(Instant now) {
        if (status == LoanOfferStatus.PENDING && !now.isBefore(expiresAt)) {
            status = LoanOfferStatus.EXPIRED;
            return true;
        }
        return false;
    }

    private void ensurePending(Instant now) {
        Objects.requireNonNull(now);
        if (status != LoanOfferStatus.PENDING) throw new InvalidLoanOfferStateException("Offer is not pending: " + status);
    }
    private static BigDecimal positive(BigDecimal n, String field) {
        Objects.requireNonNull(n, field);
        if (n.signum() <= 0) throw new IllegalArgumentException(field + " must be greater than zero");
        return n;
    }

    public UUID id(){return id;} public UUID loanApplicationId(){return loanApplicationId;}
    public UUID customerId(){return customerId;} public Money principal(){return principal;}
    public LoanTerm term(){return term;} public InterestRate annualInterestRate(){return annualInterestRate;}
    public BigDecimal monthlyInstallment(){return monthlyInstallment;} public BigDecimal totalRepayment(){return totalRepayment;}
    public LoanOfferStatus status(){return status;} public Instant createdAt(){return createdAt;}
    public Instant expiresAt(){return expiresAt;} public Instant acceptedAt(){return acceptedAt;} public Instant declinedAt(){return declinedAt;}
}
