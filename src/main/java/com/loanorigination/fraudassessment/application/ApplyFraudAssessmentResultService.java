package com.loanorigination.fraudassessment.application;

import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;

@ApplicationScoped
public class ApplyFraudAssessmentResultService {
  private final FraudAssessmentRepository fraud;
  private final LoanApplicationRepository applications;
  private final FraudAssessmentGateService gate;
  private final WorkflowEventRecorder events;
  @Inject public ApplyFraudAssessmentResultService(FraudAssessmentRepository f,LoanApplicationRepository a,
      FraudAssessmentGateService g,WorkflowEventRecorder e) { fraud=f; applications=a; gate=g; events=e; }
  @Transactional public void apply(FraudAssessmentResult result) {
    var app=applications.findByIdForUpdate(result.loanApplicationId())
        .orElseThrow(() -> new LoanApplicationNotFoundException(result.loanApplicationId()));
    var duplicate=fraud.findByAssessmentId(result.fraudAssessmentId());
    if (duplicate.isPresent()) {
      if (!duplicate.get().equals(result)) throw new IllegalArgumentException("Fraud assessment event ID was reused with different data");
      return;
    }
    fraud.save(result);
    events.record("FraudAssessmentCompleted",com.loanorigination.audit.domain.AuditAction.FRAUD_ASSESSMENT_COMPLETED,
        "LoanApplication",app.id(),Map.of("loanApplicationId",app.id().toString(),
            "fraudAssessmentId",result.fraudAssessmentId().toString(),"decision",result.decision().name(),
            "riskLevel",result.riskLevel(),"fraudScore",Integer.toString(result.fraudScore())));
    gate.approveIfBothAutomatedGatesPass(app);
  }
}
