package com.loanorigination.observability.application.port.out;

public interface OperationalMetricsPort {
    void increment(String metric);
}
