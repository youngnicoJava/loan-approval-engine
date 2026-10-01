package com.loanorigination.correlation.adapter.in.rest;

import io.quarkus.vertx.http.runtime.filters.Filters;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.MDC;

import java.util.UUID;
import java.util.regex.Pattern;

@ApplicationScoped
public class CorrelationIdHttpFilter {
    private static final String HEADER="X-Correlation-ID";
    private static final Pattern VALID=Pattern.compile("[A-Za-z0-9._:-]{1,128}");

    void register(@Observes Filters filters){
        filters.register(this::filter,Integer.MAX_VALUE);
    }

    private void filter(RoutingContext context){
        String incoming=context.request().getHeader(HEADER);
        String id=incoming!=null&&VALID.matcher(incoming).matches()?incoming:UUID.randomUUID().toString();
        context.put("correlationId",id);
        context.request().headers().set(HEADER,id);
        context.response().putHeader(HEADER,id);
        context.addHeadersEndHandler(ignored -> context.response().putHeader(HEADER,id));
        MDC.put("correlationId",id);
        context.addEndHandler(ignored->MDC.remove("correlationId"));
        context.next();
    }
}
