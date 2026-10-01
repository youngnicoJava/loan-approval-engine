package com.loanorigination.riskassessment.adapter.in.rest;

import com.loanorigination.riskassessment.domain.RiskAssessment;
import com.loanorigination.riskassessment.domain.RiskAssessmentReasonCode;
import com.loanorigination.riskassessment.domain.RiskAssessmentSource;
import com.loanorigination.riskassessment.domain.RiskDecision;

import java.time.Instant;
import java.util.UUID;

public record RiskAssessmentResponse(
        UUID id,
        UUID loanApplicationId,
        RiskDecision decision,
        RiskAssessmentReasonCode reasonCode,
        RiskAssessmentSource source,
        Instant assessedAt
) {

    public static RiskAssessmentResponse from(
            RiskAssessment assessment
    ) {

        return new RiskAssessmentResponse(
                assessment.id(),
                assessment.loanApplicationId(),
                assessment.decision(),
                assessment.reasonCode(),
                assessment.source(),
                assessment.assessedAt()
        );
    }
}