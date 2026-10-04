package com.loanorigination.riskassessment.application.port.in;

import com.loanorigination.riskassessment.domain.RiskAssessment;
import java.util.Optional;
import java.util.UUID;

public interface GetLatestRiskAssessmentUseCase {
    Optional<RiskAssessment> getLatest(UUID loanApplicationId);
}
