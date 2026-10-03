package com.loanorigination.fraudassessment.application;

import com.loanorigination.fraudassessment.domain.FraudGatePolicy;
import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentRepository;
import com.loanorigination.riskassessment.domain.RiskDecision;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Clock;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FraudAssessmentGateService {
  private final FraudAssessmentRepository fraud;
  private final RiskAssessmentRepository credit;
  private final LoanApplicationRepository applications;
  private final Clock clock;
  private final String mode;
  private final com.loanorigination.audit.application.service.WorkflowEventRecorder events;
  @Inject public FraudAssessmentGateService(FraudAssessmentRepository fraud,RiskAssessmentRepository credit,
      LoanApplicationRepository applications,Clock clock,
      @ConfigProperty(name="app.fraud.mode",defaultValue="LOCAL") String mode,
      com.loanorigination.audit.application.service.WorkflowEventRecorder events) {
    this.fraud=fraud; this.credit=credit; this.applications=applications; this.clock=clock; this.mode=mode; this.events=events;
  }
  public boolean required() { return "KAFKA".equalsIgnoreCase(mode); }
  public boolean isCleared(UUID applicationId) {
    if (!required()) return true;
    return fraud.findLatestByApplicationId(applicationId)
        .map(r -> FraudGatePolicy.isCleared(r.decision())).orElse(false);
  }
  public void assertCleared(UUID applicationId) {
    if (!isCleared(applicationId)) throw new FraudAssessmentGateNotClearedException(applicationId);
  }
  public void approveIfBothAutomatedGatesPass(LoanApplication application) {
    if (!required() || application.status()!=LoanApplicationStatus.UNDER_REVIEW) return;
    boolean creditApproved=credit.findLatestByLoanApplicationId(application.id())
        .map(r -> r.decision()==RiskDecision.APPROVE).orElse(false);
    if (creditApproved && isCleared(application.id())) {
      application.approve(clock.instant());
      applications.save(application);
      events.record("LoanApplicationApproved",com.loanorigination.audit.domain.AuditAction.APPLICATION_APPROVED,
          "LoanApplication",application.id(),java.util.Map.of("loanApplicationId",application.id().toString(),"source","CREDIT_AND_FRAUD_GATES"));
    }
  }
}
