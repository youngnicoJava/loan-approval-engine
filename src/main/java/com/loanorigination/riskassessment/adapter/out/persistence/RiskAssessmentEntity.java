package com.loanorigination.riskassessment.adapter.out.persistence;

import com.loanorigination.riskassessment.domain.RiskAssessment;
import com.loanorigination.riskassessment.domain.RiskAssessmentReasonCode;
import com.loanorigination.riskassessment.domain.RiskAssessmentSource;
import com.loanorigination.riskassessment.domain.RiskDecision;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Modelo persistente de una evaluación de riesgo.
 *
 * Se mantiene separado del modelo de dominio para evitar que Hibernate
 * forme parte del core de negocio.
 */
@Entity
@Table(name = "risk_assessments")
public class RiskAssessmentEntity extends PanacheEntityBase {

    @Id
    private UUID id;

    @Column(name = "loan_application_id", nullable = false)
    private UUID loanApplicationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskDecision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code", nullable = false, length = 80)
    private RiskAssessmentReasonCode reasonCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RiskAssessmentSource source;

    @Column(name = "assessed_at", nullable = false)
    private Instant assessedAt;

    protected RiskAssessmentEntity() {
        // Constructor requerido por JPA.
    }

    public static RiskAssessmentEntity fromDomain(
            RiskAssessment riskAssessment
    ) {

        RiskAssessmentEntity entity =
                new RiskAssessmentEntity();

        entity.id = riskAssessment.id();
        entity.loanApplicationId =
                riskAssessment.loanApplicationId();
        entity.decision =
                riskAssessment.decision();
        entity.reasonCode =
                riskAssessment.reasonCode();
        entity.source =
                riskAssessment.source();
        entity.assessedAt =
                riskAssessment.assessedAt();

        return entity;
    }

    public RiskAssessment toDomain() {

        return new RiskAssessment(
                id,
                loanApplicationId,
                decision,
                reasonCode,
                source,
                assessedAt
        );
    }
}