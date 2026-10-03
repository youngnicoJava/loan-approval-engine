package com.loanorigination.riskassessment.application.service;
import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;
import com.loanorigination.correlation.application.port.out.CorrelationIdPort;
import com.loanorigination.riskassessment.application.port.in.RequestExternalRiskAssessmentUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.UUID;
@ApplicationScoped public class RequestExternalRiskAssessmentService implements RequestExternalRiskAssessmentUseCase {
 private final PrepareLoanApplicationForRiskService prepare;private final WorkflowEventRecorder events;private final CorrelationIdPort correlation;
 @Inject public RequestExternalRiskAssessmentService(PrepareLoanApplicationForRiskService p,WorkflowEventRecorder e,CorrelationIdPort c){prepare=p;events=e;correlation=c;}
 @Override @Transactional public void request(UUID id){var app=prepare.prepare(id);var profile=app.financialProfile();if(profile==null)throw new IllegalStateException("Financial profile is required for an external risk assessment");events.record("loan.risk-assessment.requested",2,AuditAction.RISK_ASSESSMENT_REQUESTED,"LoanApplication",id,Map.ofEntries(Map.entry("assessmentRequestId",id.toString()),Map.entry("loanApplicationId",id.toString()),Map.entry("customerReference",app.customerId().toString()),Map.entry("requestedAmount",app.requestedAmount().amount().toPlainString()),Map.entry("currency",app.requestedAmount().currency().getCurrencyCode()),Map.entry("termMonths",app.term().months()),Map.entry("productType",app.productType().name()),Map.entry("monthlyIncome",profile.monthlyIncome().amount().toPlainString()),Map.entry("existingMonthlyDebtObligations",profile.existingMonthlyDebtObligations().amount().toPlainString()),Map.entry("employmentStatus",profile.employmentStatus().name()),Map.entry("employmentTenureMonths",profile.employmentTenureMonths()),Map.entry("correlationId",correlation.current())));}
}
