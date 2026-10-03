package com.loanorigination.riskassessment.application.port.out;

import com.loanorigination.riskassessment.domain.RiskAssessment;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de evaluaciones de riesgo.
 */
public interface RiskAssessmentRepository {

    void save(RiskAssessment riskAssessment);

    Optional<RiskAssessment> findById(UUID assessmentId);

    Optional<RiskAssessment> findLatestByLoanApplicationId(
            UUID loanApplicationId
    );
}
