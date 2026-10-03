package com.loanorigination.fraudassessment.adapter.out.persistence;

import com.loanorigination.fraudassessment.domain.FraudCaseResolutionRecord;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fraud_case_resolutions")
public class FraudCaseResolutionEntity extends PanacheEntityBase {
    @Id
    @Column(name = "fraud_case_id")
    public UUID fraudCaseId;

    @Column(name = "event_id", nullable = false, unique = true)
    public UUID eventId;

    @Column(name = "fraud_assessment_id", nullable = false, unique = true)
    public UUID fraudAssessmentId;

    @Column(name = "loan_application_id", nullable = false)
    public UUID loanApplicationId;

    @Column(nullable = false, length = 24)
    public String resolution;

    @Column(name = "resolved_at", nullable = false)
    public Instant resolvedAt;

    @Column(name = "correlation_id", nullable = false, length = 128)
    public String correlationId;

    public FraudCaseResolutionRecord toDomain() {
        return new FraudCaseResolutionRecord(fraudCaseId, eventId, fraudAssessmentId,
                loanApplicationId, resolution, resolvedAt, correlationId);
    }

    public static FraudCaseResolutionEntity from(FraudCaseResolutionRecord record) {
        var entity = new FraudCaseResolutionEntity();
        entity.fraudCaseId = record.fraudCaseId();
        entity.eventId = record.eventId();
        entity.fraudAssessmentId = record.fraudAssessmentId();
        entity.loanApplicationId = record.loanApplicationId();
        entity.resolution = record.resolution();
        entity.resolvedAt = record.occurredAt();
        entity.correlationId = record.correlationId();
        return entity;
    }
}
