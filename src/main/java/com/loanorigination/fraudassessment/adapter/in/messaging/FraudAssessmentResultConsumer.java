package com.loanorigination.fraudassessment.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanorigination.correlation.application.port.out.CorrelationIdPort;
import com.loanorigination.fraudassessment.application.ApplyFraudAssessmentResultService;
import com.loanorigination.fraudassessment.domain.FraudAssessmentDecision;
import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.faulttolerance.Retry;

@ApplicationScoped
public class FraudAssessmentResultConsumer {
  private final ObjectMapper mapper;
  private final ApplyFraudAssessmentResultService apply;
  private final CorrelationIdPort correlation;
  @Inject public FraudAssessmentResultConsumer(ObjectMapper m,ApplyFraudAssessmentResultService a,CorrelationIdPort c) {
    mapper=m; apply=a; correlation=c;
  }
  @Incoming("fraud-assessment-results") @ActivateRequestContext
  @Retry(maxRetries = 3, delay = 1, delayUnit = java.time.temporal.ChronoUnit.SECONDS)
  public void consume(String json) {
    try {
      JsonNode root=mapper.readTree(json);
      if (!"fraud.assessment.completed.v1".equals(root.path("eventType").asText()) || root.path("eventVersion").asInt()!=1)
        throw new IllegalArgumentException("Unsupported fraud result event contract");
      String correlationId=root.path("correlationId").asText();
      if (!correlationId.matches("[A-Za-z0-9._:-]{1,128}")) throw new IllegalArgumentException("Invalid correlation ID");
      correlation.set(correlationId);
      JsonNode p=root.path("payload");
      var result=new FraudAssessmentResult(UUID.fromString(p.path("fraudAssessmentId").asText()),
          UUID.fromString(p.path("assessmentRequestId").asText()),UUID.fromString(p.path("loanApplicationId").asText()),
          FraudAssessmentDecision.valueOf(p.path("decision").asText()),p.path("fraudScore").asInt(),
          p.path("riskLevel").asText(),mapper.convertValue(p.path("reasonCodes"),mapper.getTypeFactory().constructCollectionType(List.class,String.class)),
          p.path("rulesetId").asText(),p.path("rulesetVersion").asText(),Instant.parse(p.path("evaluatedAt").asText()),correlationId);
      apply.apply(result);
    } catch(Exception e) { throw new IllegalArgumentException("Malformed or unsupported fraud result event v1",e); }
  }
}
