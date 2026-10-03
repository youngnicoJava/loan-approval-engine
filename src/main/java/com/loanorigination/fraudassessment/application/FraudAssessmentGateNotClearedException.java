package com.loanorigination.fraudassessment.application;

public class FraudAssessmentGateNotClearedException extends RuntimeException {
  public FraudAssessmentGateNotClearedException(java.util.UUID id) {
    super("Fraud assessment has not cleared the application: " + id);
  }
}
