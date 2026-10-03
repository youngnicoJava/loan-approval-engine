package com.loanorigination.fraudassessment.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanorigination.correlation.application.port.out.CorrelationIdPort;
import com.loanorigination.fraudassessment.application.ApplyFraudCaseResolutionService;
import com.loanorigination.fraudassessment.domain.FraudCaseResolutionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class FraudCaseResolutionConsumer {
    private static final Set<String> RESOLUTIONS = Set.of("CLEARED", "CONFIRMED_FRAUD");
    private final ObjectMapper mapper;
    private final ApplyFraudCaseResolutionService apply;
    private final CorrelationIdPort correlation;

    @Inject
    public FraudCaseResolutionConsumer(ObjectMapper mapper, ApplyFraudCaseResolutionService apply,
                                       CorrelationIdPort correlation) {
        this.mapper = mapper;
        this.apply = apply;
        this.correlation = correlation;
    }

    @Incoming("fraud-case-resolutions")
    @ActivateRequestContext
    @Retry(maxRetries = 3, delay = 1, delayUnit = java.time.temporal.ChronoUnit.SECONDS)
    public void consume(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            if (!"fraud.case.resolved.v1".equals(root.path("eventType").asText())
                    || root.path("eventVersion").asInt() != 1) {
                throw new IllegalArgumentException("Unsupported fraud case resolution contract");
            }
            String correlationId = root.path("correlationId").asText();
            if (!correlationId.matches("[A-Za-z0-9._:-]{1,128}")) {
                throw new IllegalArgumentException("Invalid correlation ID");
            }
            correlation.set(correlationId);
            JsonNode payload = root.path("payload");
            String resolution = payload.path("resolution").asText();
            if (!RESOLUTIONS.contains(resolution)) throw new IllegalArgumentException("Unsupported fraud case resolution");
            apply.apply(new FraudCaseResolutionRecord(
                    UUID.fromString(payload.path("fraudCaseId").asText()),
                    UUID.fromString(root.path("eventId").asText()),
                    UUID.fromString(payload.path("fraudAssessmentId").asText()),
                    UUID.fromString(payload.path("loanApplicationId").asText()),
                    resolution,
                    Instant.parse(payload.path("resolvedAt").asText()),
                    correlationId));
        } catch (Exception failure) {
            throw new IllegalArgumentException("Malformed or unsupported fraud case resolution v1", failure);
        }
    }
}
