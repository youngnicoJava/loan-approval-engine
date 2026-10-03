package com.loanorigination.riskassessment.adapter.out.messaging;
import com.loanorigination.outbox.application.port.out.EventPublisherPort;
import com.loanorigination.outbox.domain.IntegrationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
/** Enruta la orden versionada de evaluación; los demás eventos conservan el manejo local actual. */
@ApplicationScoped public class KafkaRiskAssessmentPublisher implements EventPublisherPort {private static final Logger LOG=Logger.getLogger(KafkaRiskAssessmentPublisher.class);private final Emitter<Record<String,String>> emitter;private final ObjectMapper mapper;private final org.eclipse.microprofile.config.Config config;
 @Inject public KafkaRiskAssessmentPublisher(@Channel("risk-assessment-requests")Emitter<Record<String,String>> e,ObjectMapper m,org.eclipse.microprofile.config.Config c){emitter=e;mapper=m;config=c;}
 public void publish(IntegrationEvent e){String mode=config.getOptionalValue("app.risk.mode",String.class).orElse("LOCAL");if(!"KAFKA".equalsIgnoreCase(mode)||!"loan.risk-assessment.requested.v2".equals(e.eventType())){LOG.infof("Handled integration event locally eventId=%s eventType=%s",e.eventId(),e.eventType());return;}try{var root=mapper.createObjectNode().put("eventId",e.eventId().toString()).put("eventType",e.eventType()).put("eventVersion",2).put("occurredAt",e.occurredAt().toString()).put("aggregateType",e.aggregateType()).put("aggregateId",e.aggregateId().toString()).put("correlationId",e.correlationId());root.set("payload",mapper.readTree(e.payload()));emitter.send(Record.of(e.aggregateId().toString(),mapper.writeValueAsString(root))).toCompletableFuture().join();}catch(Exception ex){throw new IllegalStateException("Could not encode risk assessment request v2 event",ex);}}
}
