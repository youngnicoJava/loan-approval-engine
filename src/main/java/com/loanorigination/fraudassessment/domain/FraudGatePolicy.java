package com.loanorigination.fraudassessment.domain;

public final class FraudGatePolicy {
  private FraudGatePolicy() {}
  public static boolean isCleared(FraudAssessmentDecision decision) {
    return decision == FraudAssessmentDecision.PASS;
  }
}
