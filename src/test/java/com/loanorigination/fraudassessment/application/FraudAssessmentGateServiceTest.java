package com.loanorigination.fraudassessment.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loanorigination.fraudassessment.domain.FraudAssessmentDecision;
import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import com.loanorigination.fraudassessment.domain.FraudCaseResolutionRecord;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FraudAssessmentGateServiceTest {
  @Test
  void manualClearancePassesGateWithoutRewritingAutomatedReview() {
    UUID applicationId = UUID.randomUUID();
    FraudAssessmentResult automatedReview =
        new FraudAssessmentResult(
            UUID.randomUUID(),
            UUID.randomUUID(),
            applicationId,
            FraudAssessmentDecision.REVIEW,
            35,
            "MEDIUM",
            List.of("HIGH_APPLICATION_VELOCITY"),
            "loan-origination-fraud",
            "1.0.0",
            Instant.parse("2026-10-03T00:00:00Z"),
            "corr-automated");
    FraudAssessmentRepository assessments =
        new FraudAssessmentRepository() {
          @Override
          public void save(FraudAssessmentResult result) {}

          @Override
          public Optional<FraudAssessmentResult> findByAssessmentId(UUID id) {
            return Optional.empty();
          }

          @Override
          public Optional<FraudAssessmentResult> findLatestByApplicationId(UUID id) {
            return id.equals(applicationId) ? Optional.of(automatedReview) : Optional.empty();
          }
        };
    FraudCaseResolutionRepository resolutions =
        new FraudCaseResolutionRepository() {
          @Override
          public Optional<FraudCaseResolutionRecord> findLatestByLoanApplicationId(UUID id) {
            return id.equals(applicationId)
                ? Optional.of(
                    new FraudCaseResolutionRecord(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        automatedReview.fraudAssessmentId(),
                        applicationId,
                        "CLEARED",
                        Instant.parse("2026-10-03T00:01:00Z"),
                        "corr-resolution"))
                : Optional.empty();
          }

          @Override
          public Optional<FraudCaseResolutionRecord> findByFraudCaseId(UUID id) {
            return Optional.empty();
          }

          @Override
          public void save(FraudCaseResolutionRecord resolution) {}
        };
    FraudAssessmentGateService gate =
        new FraudAssessmentGateService(
            assessments,
            resolutions,
            unusedCreditRepository(),
            unusedApplicationRepository(),
            Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
            "KAFKA",
            null);

    assertTrue(gate.isCleared(applicationId));
    assertFalse(gate.isCleared(UUID.randomUUID()));
    assertTrue(automatedReview.decision() == FraudAssessmentDecision.REVIEW);
  }

  private static RiskAssessmentRepository unusedCreditRepository() {
    return new RiskAssessmentRepository() {
      @Override
      public void save(com.loanorigination.riskassessment.domain.RiskAssessment assessment) {}

      @Override
      public Optional<com.loanorigination.riskassessment.domain.RiskAssessment> findById(
          UUID assessmentId) {
        return Optional.empty();
      }

      @Override
      public Optional<com.loanorigination.riskassessment.domain.RiskAssessment>
          findLatestByLoanApplicationId(UUID loanApplicationId) {
        return Optional.empty();
      }
    };
  }

  private static LoanApplicationRepository unusedApplicationRepository() {
    return new LoanApplicationRepository() {
      @Override
      public void save(com.loanorigination.loanapplication.domain.LoanApplication application) {}

      @Override
      public Optional<com.loanorigination.loanapplication.domain.LoanApplication> findById(
          UUID id) {
        return Optional.empty();
      }

      @Override
      public Optional<com.loanorigination.loanapplication.domain.LoanApplication> findByIdForUpdate(
          UUID id) {
        return Optional.empty();
      }

      @Override
      public List<com.loanorigination.loanapplication.domain.LoanApplication> findByCustomerId(
          UUID customerId) {
        return List.of();
      }

      @Override
      public List<com.loanorigination.loanapplication.domain.LoanApplication> findForQueue(
          com.loanorigination.loanapplication.domain.LoanApplicationStatus status,
          int offset,
          int limit) {
        return List.of();
      }
    };
  }
}
