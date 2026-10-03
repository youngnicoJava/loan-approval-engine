package com.loanorigination.loanapplication.adapter.out.persistence;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.loanapplication.domain.ApplicantFinancialProfile;
import com.loanorigination.loanapplication.domain.ApplicantEmploymentStatus;
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

    @Column(name = "monthly_income", precision = 19, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "existing_monthly_debt_obligations", precision = 19, scale = 2)
    private BigDecimal existingMonthlyDebtObligations;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", length = 30)
    private ApplicantEmploymentStatus employmentStatus;

    @Column(name = "employment_tenure_months")
    private Integer employmentTenureMonths;

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
        this.monthlyIncome = loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().monthlyIncome().amount();
        this.existingMonthlyDebtObligations = loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().existingMonthlyDebtObligations().amount();
        this.employmentStatus = loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().employmentStatus();
        this.employmentTenureMonths = loanApplication.financialProfile() == null ? null : loanApplication.financialProfile().employmentTenureMonths();
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
                financialProfile(),
                status,
                createdAt,
                submittedAt,
                updatedAt
        );
    }

    private ApplicantFinancialProfile financialProfile() {
        if (monthlyIncome == null || existingMonthlyDebtObligations == null || employmentStatus == null || employmentTenureMonths == null) return null;
        return new ApplicantFinancialProfile(Money.of(monthlyIncome, Currency.getInstance(currency)), Money.of(existingMonthlyDebtObligations, Currency.getInstance(currency)), employmentStatus, employmentTenureMonths);
    }
}
