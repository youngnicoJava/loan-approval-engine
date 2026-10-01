package com.loanorigination.riskassessment.application.port.in;

import com.loanorigination.riskassessment.domain.RiskAssessment;

import java.util.UUID;

public interface EvaluateLoanApplicationUseCase {

    RiskAssessment evaluate(UUID loanApplicationId);
}