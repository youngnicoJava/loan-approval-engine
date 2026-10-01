package com.loanorigination.outbox.adapter.out.persistence;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.runtime.Startup;

/** Métricas agregadas del outbox; no publican identificadores de eventos como etiquetas. */
@ApplicationScoped
@Startup
public class OutboxMetrics {
    private final OutboxPanacheRepository repository;

    @Inject
    public OutboxMetrics(OutboxPanacheRepository repository, MeterRegistry registry) {
        this.repository = repository;
        registry.gauge("loan_outbox_pending", this, metrics -> metrics.count("PENDING"));
        registry.gauge("loan_outbox_processing", this, metrics -> metrics.count("PROCESSING"));
        registry.gauge("loan_outbox_published", this, metrics -> metrics.count("PUBLISHED"));
        registry.gauge("loan_outbox_failed", this, metrics -> metrics.count("FAILED"));
        registry.gauge("loan_outbox_attempts_total", this, OutboxMetrics::attempts);
    }

    private long count(String status) {
        return repository.count("status", com.loanorigination.outbox.domain.OutboxStatus.valueOf(status));
    }

    private double attempts() {
        return repository.getEntityManager()
                .createQuery("select coalesce(sum(event.attemptCount), 0) from OutboxEventEntity event", Long.class)
                .getSingleResult();
    }
}
