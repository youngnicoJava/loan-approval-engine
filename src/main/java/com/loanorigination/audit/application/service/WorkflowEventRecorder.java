package com.loanorigination.audit.application.service;

import com.loanorigination.audit.application.port.out.AuditRepository;
import com.loanorigination.audit.domain.*;
import com.loanorigination.correlation.application.port.out.CorrelationIdPort;
import com.loanorigination.outbox.application.port.out.OutboxRepository;
import com.loanorigination.outbox.domain.OutboxEvent;
import com.loanorigination.outbox.domain.OutboxStatus;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.security.domain.AuthenticatedUser;
import com.loanorigination.shared.application.port.out.PayloadCodecPort;
import com.loanorigination.shared.domain.event.DomainEvent;
import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Clock;
import java.util.Map;
import java.util.UUID;

/** Registra en una misma transacción la evidencia funcional y el mensaje pendiente de publicación. */
@ApplicationScoped
public class WorkflowEventRecorder {
    private final AuditRepository audit;
    private final OutboxRepository outbox;
    private final CurrentUserPort currentUser;
    private final CorrelationIdPort correlation;
    private final PayloadCodecPort codec;
    private final Clock clock;

    @Inject public WorkflowEventRecorder(AuditRepository audit,OutboxRepository outbox,CurrentUserPort currentUser,
            CorrelationIdPort correlation,PayloadCodecPort codec,Clock clock){
        this.audit=audit;this.outbox=outbox;this.currentUser=currentUser;this.correlation=correlation;this.codec=codec;this.clock=clock;
    }

    public void record(String eventType,AuditAction action,String aggregateType,UUID aggregateId,Map<String,Object> payload){
        var time=clock.instant();
        UUID eventId=UUID.randomUUID();
        String correlationId;
        try { correlationId=correlation.current(); }
        catch (ContextNotActiveException noRequest) { correlationId=UUID.randomUUID().toString(); }
        DomainEvent event=new DomainEvent(eventId,eventType,1,time,aggregateType,aggregateId,correlationId,payload);
        String json=codec.encode(event.payload());
        AuditActorType actorType=AuditActorType.SYSTEM;
        String actorId="system";
        try {
            AuthenticatedUser user=currentUser.getCurrentUser();
            actorId=user.externalIdentityId();
            actorType=actorType(user);
        } catch (IllegalStateException | ContextNotActiveException noAuthenticatedActor) { }
        audit.append(new AuditEvent(UUID.randomUUID(),event.eventId(),actorId,actorType,action,event.aggregateType(),event.aggregateId(),event.occurredAt(),event.correlationId(),json));
        outbox.append(new OutboxEvent(UUID.randomUUID(),event.eventId(),event.eventType(),event.eventVersion(),event.aggregateType(),event.aggregateId(),event.correlationId(),json,event.occurredAt(),null,OutboxStatus.PENDING,0,null,null));
    }

    private AuditActorType actorType(AuthenticatedUser user){
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.ADMIN))return AuditActorType.ADMIN;
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.LOAN_OFFICER))return AuditActorType.LOAN_OFFICER;
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.AUDITOR))return AuditActorType.AUDITOR;
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.CUSTOMER))return AuditActorType.CUSTOMER;
        return AuditActorType.SYSTEM;
    }
}
