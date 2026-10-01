package com.loanorigination.riskassessment.domain;

import java.util.Objects;

/**
 * Resultado producido por un motor de evaluación.
 *
 * Es independiente de cómo se obtuvo la evaluación.
 * Puede provenir de reglas locales, HTTP, Kafka u otro adapter.
 */
public record RiskAssessmentResult(
        RiskDecision decision,
        RiskAssessmentReasonCode reasonCode,
        RiskAssessmentSource source
) {

    public RiskAssessmentResult {
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
    }
}