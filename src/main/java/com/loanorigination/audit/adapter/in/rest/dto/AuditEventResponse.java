package com.loanorigination.audit.adapter.in.rest.dto;
import com.loanorigination.audit.domain.AuditEvent;
import com.loanorigination.audit.domain.AuditAction;
import com.loanorigination.audit.domain.AuditActorType;
import java.time.Instant;
import java.util.UUID;
public record AuditEventResponse(UUID id,UUID eventId,String actorId,AuditActorType actorType,AuditAction action,
        String aggregateType,UUID aggregateId,Instant occurredAt,String correlationId,String metadata){
 public static AuditEventResponse from(AuditEvent e){return new AuditEventResponse(e.id(),e.eventId(),e.actorId(),e.actorType(),e.action(),e.aggregateType(),e.aggregateId(),e.occurredAt(),e.correlationId(),e.metadata());}
}
