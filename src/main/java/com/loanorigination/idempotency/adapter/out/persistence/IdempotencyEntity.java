package com.loanorigination.idempotency.adapter.out.persistence;
import com.loanorigination.idempotency.domain.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="idempotency_records")
public class IdempotencyEntity {
 @Id public UUID id;
 @Column(name="actor_id",nullable=false) public String actorId;
 @Column(nullable=false) public String scope;
 @Column(name="idempotency_key",nullable=false) public String idempotencyKey;
 @Column(name="request_hash",nullable=false,length=64) public String requestHash;
 @Column(name="response_status") public Integer responseStatus;
 @Column(name="response_body") public String responseBody;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public IdempotencyStatus status;
 @Column(name="created_at",nullable=false) public Instant createdAt;
 @Column(name="expires_at",nullable=false) public Instant expiresAt;
 protected IdempotencyEntity() { }
 IdempotencyRecord toDomain(){return new IdempotencyRecord(id,actorId,scope,idempotencyKey,requestHash,responseStatus,responseBody,status,createdAt,expiresAt);}
}
