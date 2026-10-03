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
import com.loanorigination.observability.application.port.out.OperationalMetricsPort;

/** Registra en una misma transacción la evidencia funcional y el mensaje pendiente de publicación. */
@ApplicationScoped
public class WorkflowEventRecorder {
    private final AuditRepository audit;
    private final OutboxRepository outbox;
    private final CurrentUserPort currentUser;
    private final CorrelationIdPort correlation;
    private final PayloadCodecPort codec;
    private final Clock clock;
    private final OperationalMetricsPort metrics;

    @Inject public WorkflowEventRecorder(AuditRepository audit,OutboxRepository outbox,CurrentUserPort currentUser,
            CorrelationIdPort correlation,PayloadCodecPort codec,Clock clock,OperationalMetricsPort metrics){
        this.audit=audit;this.outbox=outbox;this.currentUser=currentUser;this.correlation=correlation;this.codec=codec;this.clock=clock;this.metrics=metrics;
    }

    public void record(String eventType,AuditAction action,String aggregateType,UUID aggregateId,Map<String,Object> payload){
        record(eventType,1,action,aggregateType,aggregateId,payload);
    }

    public void record(String eventType,int eventVersion,AuditAction action,String aggregateType,UUID aggregateId,Map<String,Object> payload){
        var time=clock.instant();
        UUID eventId=UUID.randomUUID();
        String correlationId;
        try { correlationId=correlation.current(); }
        catch (ContextNotActiveException noRequest) { correlationId=UUID.randomUUID().toString(); }
        DomainEvent event=new DomainEvent(eventId,eventType,eventVersion,time,aggregateType,aggregateId,correlationId,payload);
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
        String metric = switch (eventType) {
            case "LoanApplicationSubmitted" -> "loan_applications_submitted_total";
            case "RiskAssessmentCompleted" -> "risk_assessments_completed_total";
            case "LoanApplicationApproved" -> "loan_applications_approved_total";
            case "LoanApplicationRejected" -> "loan_applications_rejected_total";
            case "LoanOfferCreated" -> "loan_offers_created_total";
            case "LoanOfferAccepted" -> "loan_offers_accepted_total";
            case "LoanCreated" -> "loans_created_total";
            case "DisbursementRequested" -> "disbursements_requested_total";
            case "LoanDisbursed" -> "disbursements_completed_total";
            case "RepaymentScheduleCreated" -> "repayment_schedules_created_total";
            default -> null;
        };
        if (metric != null) metrics.increment(metric);
        if ("RiskAssessmentCompleted".equals(eventType) && "REFER".equals(payload.get("decision"))) {
            metrics.increment("loan_applications_referred_total");
        }
    }

    private AuditActorType actorType(AuthenticatedUser user){
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.ADMIN))return AuditActorType.ADMIN;
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.LOAN_OFFICER))return AuditActorType.LOAN_OFFICER;
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.AUDITOR))return AuditActorType.AUDITOR;
        if(user.hasRole(com.loanorigination.security.domain.ApplicationRole.CUSTOMER))return AuditActorType.CUSTOMER;
        return AuditActorType.SYSTEM;
    }
}
