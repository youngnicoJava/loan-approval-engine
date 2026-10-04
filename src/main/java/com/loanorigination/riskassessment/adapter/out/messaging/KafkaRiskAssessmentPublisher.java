package com.loanorigination.riskassessment.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanorigination.outbox.application.port.out.EventPublisherPort;
import com.loanorigination.outbox.domain.IntegrationEvent;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaRiskAssessmentPublisher implements EventPublisherPort {
  private static final Logger LOG = Logger.getLogger(KafkaRiskAssessmentPublisher.class);
  private final Emitter<Record<String, String>> riskEmitter;
  private final Emitter<Record<String, String>> fraudEmitter;
  private final ObjectMapper mapper;
  private final Config config;

  @Inject
  public KafkaRiskAssessmentPublisher(
      @Channel("risk-assessment-requests") Emitter<Record<String, String>> riskEmitter,
      @Channel("fraud-assessment-requests") Emitter<Record<String, String>> fraudEmitter,
      ObjectMapper mapper,
      Config config) {
    this.riskEmitter = riskEmitter;
    this.fraudEmitter = fraudEmitter;
    this.mapper = mapper;
    this.config = config;
  }

  @Override
  public void publish(IntegrationEvent event) {
    if ("loan.risk-assessment.requested.v2".equals(event.eventType())
        && "KAFKA".equalsIgnoreCase(config.getOptionalValue("app.risk.mode", String.class).orElse("LOCAL"))) {
      publish(riskEmitter, event, event.eventType(), 2);
      return;
    }
    if ("loan.fraud-assessment.requested.v1".equals(event.eventType())
        && "KAFKA".equalsIgnoreCase(config.getOptionalValue("app.fraud.mode", String.class).orElse("LOCAL"))) {
      publish(fraudEmitter, event, "loan.fraud-assessment.requested.v1", 1);
      return;
    }
    LOG.infof("Handled integration event locally eventId=%s eventType=%s", event.eventId(), event.eventType());
  }

  private void publish(Emitter<Record<String, String>> emitter, IntegrationEvent event, String type, int version) {
    try {
      var root = mapper.createObjectNode().put("eventId", event.eventId().toString())
          .put("eventType", type).put("eventVersion", version).put("occurredAt", event.occurredAt().toString())
          .put("aggregateType", event.aggregateType()).put("aggregateId", event.aggregateId().toString())
          .put("correlationId", event.correlationId());
      root.set("payload", mapper.readTree(event.payload()));
      emitter.send(Record.of(event.aggregateId().toString(), mapper.writeValueAsString(root))).toCompletableFuture().join();
    } catch (Exception ex) {
      throw new IllegalStateException("Could not encode loan risk/fraud assessment request", ex);
    }
  }
}
