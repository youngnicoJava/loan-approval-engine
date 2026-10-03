package com.loanorigination.fraudassessment.application;

import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import java.util.Optional;
import java.util.UUID;

public interface FraudAssessmentRepository {
  void save(FraudAssessmentResult result);
  Optional<FraudAssessmentResult> findByAssessmentId(UUID id);
  Optional<FraudAssessmentResult> findLatestByApplicationId(UUID applicationId);
}
