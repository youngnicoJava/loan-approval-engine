package com.loanorigination.riskassessment.application.port.in;
import java.util.UUID;
public interface RequestExternalRiskAssessmentUseCase { void request(UUID loanApplicationId); }
