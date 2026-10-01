package com.loanorigination.correlation.adapter.in.rest;

import com.loanorigination.correlation.application.port.out.CorrelationIdPort;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CorrelationIdContext implements CorrelationIdPort {
    private String correlationId;
    public void set(String value) { correlationId = value; }
    @Override public String current() { return correlationId == null ? "system" : correlationId; }
}
