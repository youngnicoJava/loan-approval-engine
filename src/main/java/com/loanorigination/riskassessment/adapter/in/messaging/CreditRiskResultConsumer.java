package com.loanorigination.riskassessment.adapter.in.messaging;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanorigination.riskassessment.application.service.FinalizeRiskAssessmentService;
import com.loanorigination.riskassessment.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.util.UUID;
import org.jboss.logging.Logger;
@ApplicationScoped public class CreditRiskResultConsumer {private static final Logger LOG=Logger.getLogger(CreditRiskResultConsumer.class);private final ObjectMapper mapper;private final FinalizeRiskAssessmentService finalizer;
 @Inject public CreditRiskResultConsumer(ObjectMapper m,FinalizeRiskAssessmentService f){mapper=m;finalizer=f;}
 @Incoming("credit-risk-results") public void consume(String json){try{JsonNode root=mapper.readTree(json);if(!"credit-risk.assessment.completed.v2".equals(root.path("eventType").asText())||root.path("eventVersion").asInt()!=2){throw new IllegalArgumentException("Unsupported credit risk result event contract");}var p=root.path("payload");var decision=RiskDecision.valueOf(p.path("decision").asText());var reasons=p.path("reasons");if(!reasons.isArray()||reasons.isEmpty())throw new IllegalArgumentException("Risk result must contain a reason");var engineCode=reasons.get(0).path("code").asText();RiskAssessmentReasonCode reason;try{reason=RiskAssessmentReasonCode.valueOf(engineCode);}catch(IllegalArgumentException unknown){reason=switch(decision){case APPROVE->RiskAssessmentReasonCode.APPROVED_BY_CREDIT_RISK_POLICY;case REJECT->RiskAssessmentReasonCode.REJECTED_BY_CREDIT_RISK_POLICY;case REFER->RiskAssessmentReasonCode.REFERRED_BY_CREDIT_RISK_POLICY;};}var result=new RiskAssessmentResult(decision,reason,RiskAssessmentSource.CREDIT_RISK_ENGINE);finalizer.finalizeExternal(UUID.fromString(p.path("riskAssessmentId").asText()),UUID.fromString(p.path("loanApplicationId").asText()),result);}catch(Exception e){throw new IllegalArgumentException("Malformed or unsupported credit risk result event v2",e);}}
}
