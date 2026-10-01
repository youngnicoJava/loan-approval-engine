package com.loanorigination.outbox.adapter.out.persistence;

import com.loanorigination.outbox.domain.OutboxEvent;
import com.loanorigination.outbox.domain.OutboxStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="outbox_events")
public class OutboxEventEntity {
    @Id public UUID id;
    @Column(name="event_id",nullable=false,unique=true) public UUID eventId;
    @Column(name="event_type",nullable=false) public String eventType;
    @Column(name="event_version",nullable=false) public int eventVersion;
    @Column(name="aggregate_type",nullable=false) public String aggregateType;
    @Column(name="aggregate_id",nullable=false) public UUID aggregateId;
    @Column(name="correlation_id",nullable=false) public String correlationId;
    @Column(nullable=false) public String payload;
    @Column(name="occurred_at",nullable=false) public Instant occurredAt;
    @Column(name="published_at") public Instant publishedAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public OutboxStatus status;
    @Column(name="attempt_count",nullable=false) public int attemptCount;
    @Column(name="locked_at") public Instant lockedAt;
    @Column(name="last_error") public String lastError;
    protected OutboxEventEntity() { }
    static OutboxEventEntity from(OutboxEvent e){var x=new OutboxEventEntity();x.id=e.id();x.eventId=e.eventId();x.eventType=e.eventType();x.eventVersion=e.eventVersion();x.aggregateType=e.aggregateType();x.aggregateId=e.aggregateId();x.correlationId=e.correlationId();x.payload=e.payload();x.occurredAt=e.occurredAt();x.publishedAt=e.publishedAt();x.status=e.status();x.attemptCount=e.attemptCount();x.lockedAt=e.lockedAt();x.lastError=e.lastError();return x;}
    OutboxEvent toDomain(){return new OutboxEvent(id,eventId,eventType,eventVersion,aggregateType,aggregateId,correlationId,payload,occurredAt,publishedAt,status,attemptCount,lockedAt,lastError);}
}
