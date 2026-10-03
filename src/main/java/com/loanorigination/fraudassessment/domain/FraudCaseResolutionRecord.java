package com.loanorigination.fraudassessment.domain;

import java.time.Instant;
import java.util.UUID;

public record FraudCaseResolutionRecord(
        UUID fraudCaseId,
        UUID eventId,
        UUID fraudAssessmentId,
        UUID loanApplicationId,
        String resolution,
        Instant occurredAt,
        String correlationId
) {}
