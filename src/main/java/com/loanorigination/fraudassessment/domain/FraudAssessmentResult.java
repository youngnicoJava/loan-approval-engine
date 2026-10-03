package com.loanorigination.fraudassessment.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudAssessmentResult(
    UUID fraudAssessmentId, UUID assessmentRequestId, UUID loanApplicationId,
    FraudAssessmentDecision decision, int fraudScore, String riskLevel,
    List<String> reasonCodes, String rulesetId, String rulesetVersion,
    Instant evaluatedAt, String correlationId) {
  public FraudAssessmentResult { reasonCodes = List.copyOf(reasonCodes); }
}
