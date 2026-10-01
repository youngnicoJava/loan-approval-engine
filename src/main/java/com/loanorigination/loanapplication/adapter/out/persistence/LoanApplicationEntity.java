package com.loanorigination.loanapplication.adapter.out.persistence;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

/**
 * Representación de persistencia de LoanApplication.
 *
 * Esta clase pertenece a infraestructura y puede conocer JPA/Panache.
 * El agregado de dominio no contiene ninguna anotación de persistencia.
 */
@Entity
@Table(name = "loan_applications")
public class LoanApplicationEntity extends PanacheEntityBase {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 50)
    private LoanProductType productType;

    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoanApplicationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LoanApplicationEntity() {
        // Constructor requerido por JPA.
    }

    public static LoanApplicationEntity fromDomain(LoanApplication loanApplication) {

        LoanApplicationEntity entity = new LoanApplicationEntity();

        entity.id = loanApplication.id();
        entity.updateFromDomain(loanApplication);

        return entity;
    }

    /**
     * Copiamos el estado del agregado a la representación persistente.
     *
     * La entidad no decide qué transiciones son válidas; solamente
     * refleja el estado que ya fue validado por el dominio.
     */
    public void updateFromDomain(LoanApplication loanApplication) {

        this.customerId = loanApplication.customerId();
        this.productType = loanApplication.productType();
        this.requestedAmount = loanApplication.requestedAmount().amount();
        this.currency = loanApplication.requestedAmount().currency().getCurrencyCode();
        this.termMonths = loanApplication.term().months();
        this.purpose = loanApplication.purpose();
        this.status = loanApplication.status();
        this.createdAt = loanApplication.createdAt();
        this.submittedAt = loanApplication.submittedAt();
        this.updatedAt = loanApplication.updatedAt();
    }

    public LoanApplication toDomain() {

        return LoanApplication.restore(
                id,
                customerId,
                productType,
                Money.of(
                        requestedAmount,
                        Currency.getInstance(currency)
                ),
                LoanTerm.ofMonths(termMonths),
                purpose,
                status,
                createdAt,
                submittedAt,
                updatedAt
        );
    }
}