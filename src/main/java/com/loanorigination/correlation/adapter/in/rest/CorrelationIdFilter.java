package com.loanorigination.correlation.adapter.in.rest;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Provider
@Priority(Priorities.AUTHENTICATION - 1)
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
    public static final String HEADER = "X-Correlation-ID";
    private static final Pattern VALID = Pattern.compile("[A-Za-z0-9._:-]{1,128}");
    private final CorrelationIdContext context;

    @Inject public CorrelationIdFilter(CorrelationIdContext context) { this.context = context; }

    @Override public void filter(ContainerRequestContext request) throws IOException {
        String incoming = request.getHeaderString(HEADER);
        String id = incoming != null && VALID.matcher(incoming).matches() ? incoming : UUID.randomUUID().toString();
        context.set(id);
        MDC.put("correlationId", id);
    }

    @Override public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        response.getHeaders().putSingle(HEADER, context.current());
        MDC.remove("correlationId");
    }
}
