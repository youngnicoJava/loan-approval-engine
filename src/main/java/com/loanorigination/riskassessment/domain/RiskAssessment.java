package com.loanorigination.riskassessment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una evaluación concreta realizada sobre una LoanApplication.
 *
 * Una evaluación tiene identidad propia porque necesitamos conservar
 * qué decisión fue obtenida, cuándo ocurrió y qué fuente la produjo.
 */
public record RiskAssessment(
        UUID id,
        UUID loanApplicationId,
        RiskDecision decision,
        RiskAssessmentReasonCode reasonCode,
        RiskAssessmentSource source,
        Instant assessedAt
) {

    public RiskAssessment {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(
                loanApplicationId,
                "loanApplicationId cannot be null"
        );
        Objects.requireNonNull(
                decision,
                "decision cannot be null"
        );
        Objects.requireNonNull(
                reasonCode,
                "reasonCode cannot be null"
        );
        Objects.requireNonNull(
                source,
                "source cannot be null"
        );
        Objects.requireNonNull(
                assessedAt,
                "assessedAt cannot be null"
        );
    }

    public static RiskAssessment create(
            UUID loanApplicationId,
            RiskAssessmentResult result,
            Instant assessedAt
    ) {
        return new RiskAssessment(
                UUID.randomUUID(),
                loanApplicationId,
                result.decision(),
                result.reasonCode(),
                result.source(),
                assessedAt
        );
    }

    public static RiskAssessment create(UUID assessmentId, UUID loanApplicationId, RiskAssessmentResult result, Instant assessedAt) {
        return new RiskAssessment(assessmentId, loanApplicationId, result.decision(), result.reasonCode(), result.source(), assessedAt);
    }
}
