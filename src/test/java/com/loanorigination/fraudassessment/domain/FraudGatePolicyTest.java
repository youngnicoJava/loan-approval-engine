package com.loanorigination.fraudassessment.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FraudGatePolicyTest {
  @Test void onlyPassClearsOfferGate() {
    assertTrue(FraudGatePolicy.isCleared(FraudAssessmentDecision.PASS));
    assertFalse(FraudGatePolicy.isCleared(FraudAssessmentDecision.REVIEW));
    assertFalse(FraudGatePolicy.isCleared(FraudAssessmentDecision.BLOCK));
  }
}
