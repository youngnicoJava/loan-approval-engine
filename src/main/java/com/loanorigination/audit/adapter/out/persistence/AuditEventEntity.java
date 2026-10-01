package com.loanorigination.audit.adapter.out.persistence;

import com.loanorigination.audit.domain.AuditEvent;
import com.loanorigination.audit.domain.AuditAction;
import com.loanorigination.audit.domain.AuditActorType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="audit_events")
public class AuditEventEntity {
    @Id public UUID id;
    @Column(name="event_id",nullable=false,unique=true) public UUID eventId;
    @Column(name="actor_id",nullable=false) public String actorId;
    @Enumerated(EnumType.STRING) @Column(name="actor_type",nullable=false) public AuditActorType actorType;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public AuditAction action;
    @Column(name="aggregate_type",nullable=false) public String aggregateType;
    @Column(name="aggregate_id",nullable=false) public UUID aggregateId;
    @Column(name="occurred_at",nullable=false) public Instant occurredAt;
    @Column(name="correlation_id",nullable=false) public String correlationId;
    @Column(nullable=false) public String metadata;
    protected AuditEventEntity() { }
    static AuditEventEntity from(AuditEvent e) { var x=new AuditEventEntity(); x.id=e.id(); x.eventId=e.eventId(); x.actorId=e.actorId(); x.actorType=e.actorType(); x.action=e.action(); x.aggregateType=e.aggregateType(); x.aggregateId=e.aggregateId(); x.occurredAt=e.occurredAt(); x.correlationId=e.correlationId(); x.metadata=e.metadata(); return x; }
    AuditEvent toDomain() { return new AuditEvent(id,eventId,actorId,actorType,action,aggregateType,aggregateId,occurredAt,correlationId,metadata); }
}
