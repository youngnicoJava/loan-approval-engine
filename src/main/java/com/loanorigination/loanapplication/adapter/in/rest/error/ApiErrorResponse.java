package com.loanorigination.loanapplication.adapter.in.rest.error;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String correlationId
) {

    public static ApiErrorResponse of(
            String code,
            String message
    ) {
        Object correlationId = org.jboss.logging.MDC.get("correlationId");
        return new ApiErrorResponse(code, message, Instant.now(), correlationId == null ? null : correlationId.toString());
    }
}
