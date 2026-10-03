package com.loanorigination.loanapplication.domain;

import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root de una solicitud de préstamo.
 *
 * LoanApplication representa el proceso de solicitud, no la obligación
 * financiera. La obligación nace posteriormente cuando una oferta aprobada
 * es aceptada y se crea un Loan.
 *
 * Las reglas de transición de estado viven dentro del agregado para evitar
 * que cada caso de uso tenga que conocer y repetir las mismas invariantes.
 */
public final class LoanApplication {

    private final UUID id;
    private final UUID customerId;
    private final LoanProductType productType;
    private final Money requestedAmount;
    private final LoanTerm term;
    private final String purpose;
    private final ApplicantFinancialProfile financialProfile;
    private final Instant createdAt;

    private LoanApplicationStatus status;
    private Instant submittedAt;
    private Instant updatedAt;

    private LoanApplication(
            UUID id,
            UUID customerId,
            LoanProductType productType,
            Money requestedAmount,
            LoanTerm term,
            String purpose,
            ApplicantFinancialProfile financialProfile,
            LoanApplicationStatus status,
            Instant createdAt,
            Instant submittedAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId cannot be null");
        this.productType = Objects.requireNonNull(productType, "productType cannot be null");
        this.requestedAmount = Objects.requireNonNull(requestedAmount, "requestedAmount cannot be null");
        if (requestedAmount.amount().signum() <= 0) {
            throw new IllegalArgumentException("Requested amount must be greater than zero");
        }
        this.term = Objects.requireNonNull(term, "term cannot be null");
        this.purpose = requirePurpose(purpose);
        this.financialProfile = financialProfile;
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.submittedAt = submittedAt;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * Crea una nueva solicitud en estado DRAFT.
     */
    public static LoanApplication create(
            UUID customerId,
            LoanProductType productType,
            Money requestedAmount,
            LoanTerm term,
            String purpose,
            ApplicantFinancialProfile financialProfile,
            Instant now
    ) {
        Objects.requireNonNull(now, "now cannot be null");

        return new LoanApplication(
                UUID.randomUUID(),
                customerId,
                productType,
                requestedAmount,
                term,
                purpose,
                Objects.requireNonNull(financialProfile, "financialProfile cannot be null"),
                LoanApplicationStatus.DRAFT,
                now,
                null,
                now
        );
    }

    /**
     * Reconstruye una solicitud existente desde una fuente de persistencia.
     *
     * La persistencia puede rehidratar el agregado sin exponer setters
     * ni permitir que infraestructura modifique arbitrariamente su estado.
     */
    public static LoanApplication restore(
            UUID id,
            UUID customerId,
            LoanProductType productType,
            Money requestedAmount,
            LoanTerm term,
            String purpose,
            ApplicantFinancialProfile financialProfile,
            LoanApplicationStatus status,
            Instant createdAt,
            Instant submittedAt,
            Instant updatedAt
    ) {
        return new LoanApplication(
                id,
                customerId,
                productType,
                requestedAmount,
                term,
                purpose,
                financialProfile,
                status,
                createdAt,
                submittedAt,
                updatedAt
        );
    }

    /**
     * Una solicitud solamente puede enviarse desde DRAFT.
     */
    public void submit(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");

        ensureStatus(LoanApplicationStatus.DRAFT, "Only draft applications can be submitted");
        if (financialProfile == null) {
            throw new InvalidLoanApplicationStateException("Financial profile is required before an application can be submitted");
        }

        this.status = LoanApplicationStatus.SUBMITTED;
        this.submittedAt = now;
        this.updatedAt = now;
    }

    /**
     * La revisión comienza únicamente después de que la solicitud
     * fue enviada por el cliente.
     */
    public void startReview(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");

        ensureStatus(
                LoanApplicationStatus.SUBMITTED,
                "Only submitted applications can enter review"
        );

        this.status = LoanApplicationStatus.UNDER_REVIEW;
        this.updatedAt = now;
    }

    /**
     * Aprobar una solicitud no crea todavía una obligación financiera.
     *
     * La aprobación significa que el proceso puede continuar hacia
     * la generación de una oferta.
     */
    public void approve(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");

        ensureStatus(
                LoanApplicationStatus.UNDER_REVIEW,
                "Only applications under review can be approved"
        );

        this.status = LoanApplicationStatus.APPROVED;
        this.updatedAt = now;
    }

    /**
     * Rechazar una solicitud finaliza su proceso de originación.
     */
    public void reject(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");

        ensureStatus(
                LoanApplicationStatus.UNDER_REVIEW,
                "Only applications under review can be rejected"
        );

        this.status = LoanApplicationStatus.REJECTED;
        this.updatedAt = now;
    }

    /**
     * La cancelación puede ocurrir mientras la solicitud todavía
     * está siendo procesada, pero nunca después de una decisión final.
     */
    public void cancel(Instant now) {
        Objects.requireNonNull(now, "now cannot be null");

        if (status != LoanApplicationStatus.DRAFT
                && status != LoanApplicationStatus.SUBMITTED
                && status != LoanApplicationStatus.UNDER_REVIEW) {

            throw new InvalidLoanApplicationStateException(
                    "Application cannot be cancelled from status " + status
            );
        }

        this.status = LoanApplicationStatus.CANCELLED;
        this.updatedAt = now;
    }

    private void ensureStatus(
            LoanApplicationStatus expected,
            String message
    ) {
        if (this.status != expected) {
            throw new InvalidLoanApplicationStateException(
                    message + ". Current status: " + this.status
            );
        }
    }

    private static String requirePurpose(String purpose) {
        Objects.requireNonNull(purpose, "purpose cannot be null");

        String normalized = purpose.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Purpose cannot be blank");
        }

        if (normalized.length() > 500) {
            throw new IllegalArgumentException("Purpose cannot exceed 500 characters");
        }

        return normalized;
    }

    public UUID id() {
        return id;
    }

    public UUID customerId() {
        return customerId;
    }

    public LoanProductType productType() {
        return productType;
    }

    public Money requestedAmount() {
        return requestedAmount;
    }

    public LoanTerm term() {
        return term;
    }

    public String purpose() {
        return purpose;
    }

    public ApplicantFinancialProfile financialProfile() { return financialProfile; }

    public LoanApplicationStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant submittedAt() {
        return submittedAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
