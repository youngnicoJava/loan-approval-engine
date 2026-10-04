package com.loanorigination.riskassessment.application.service;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.riskassessment.application.port.in.GetLatestRiskAssessmentUseCase;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentRepository;
import com.loanorigination.riskassessment.domain.RiskAssessment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GetLatestRiskAssessmentService implements GetLatestRiskAssessmentUseCase {
    private final LoanApplicationRepository applications;
    private final RiskAssessmentRepository assessments;

    @Inject
    public GetLatestRiskAssessmentService(LoanApplicationRepository applications, RiskAssessmentRepository assessments) {
        this.applications = applications;
        this.assessments = assessments;
    }

    @Override
    public Optional<RiskAssessment> getLatest(UUID loanApplicationId) {
        applications.findById(loanApplicationId)
                .orElseThrow(() -> new LoanApplicationNotFoundException(loanApplicationId));
        return assessments.findLatestByLoanApplicationId(loanApplicationId);
    }
}
