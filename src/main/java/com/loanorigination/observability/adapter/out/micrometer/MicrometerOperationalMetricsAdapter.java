package com.loanorigination.observability.adapter.out.micrometer;

import com.loanorigination.observability.application.port.out.OperationalMetricsPort;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MicrometerOperationalMetricsAdapter implements OperationalMetricsPort {
    private final MeterRegistry registry;

    @Inject
    public MicrometerOperationalMetricsAdapter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void increment(String metric) {
        registry.counter(metric).increment();
    }
}
