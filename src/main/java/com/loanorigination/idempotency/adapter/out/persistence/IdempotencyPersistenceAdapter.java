package com.loanorigination.idempotency.adapter.out.persistence;
import com.loanorigination.idempotency.application.port.out.IdempotencyRepository;
import com.loanorigination.idempotency.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.UUID;
@ApplicationScoped
public class IdempotencyPersistenceAdapter implements IdempotencyRepository {
 private final IdempotencyPanacheRepository repository; private final EntityManager entityManager;
 @Inject public IdempotencyPersistenceAdapter(IdempotencyPanacheRepository repository,EntityManager entityManager){this.repository=repository;this.entityManager=entityManager;}
 @Override public IdempotencyClaim claim(String actor,String scope,String key,String hash,Instant now,Instant expiresAt){
  int inserted=entityManager.createNativeQuery("INSERT INTO idempotency_records (id,actor_id,scope,idempotency_key,request_hash,status,created_at,expires_at) VALUES (:id,:actor,:scope,:key,:hash,'PROCESSING',:created,:expiry) ON CONFLICT (actor_id,scope,idempotency_key) DO NOTHING")
   .setParameter("id",UUID.randomUUID()).setParameter("actor",actor).setParameter("scope",scope).setParameter("key",key).setParameter("hash",hash).setParameter("created",now).setParameter("expiry",expiresAt).executeUpdate();
  IdempotencyEntity entity=repository.lock(actor,scope,key).orElseThrow();
  if(!entity.expiresAt.isAfter(now)){entity.requestHash=hash;entity.status=IdempotencyStatus.PROCESSING;entity.responseStatus=null;entity.responseBody=null;entity.createdAt=now;entity.expiresAt=expiresAt;entityManager.flush();return new IdempotencyClaim(entity.toDomain(),true);}
  boolean acquired=inserted==1;
  return new IdempotencyClaim(entity.toDomain(),acquired);
 }
 @Override public void complete(IdempotencyRecord record,int status,String body){var entity=repository.findById(record.id());if(entity==null)throw new IllegalStateException("Idempotency reservation disappeared");entity.status=IdempotencyStatus.COMPLETED;entity.responseStatus=status;entity.responseBody=body;entity.expiresAt=record.createdAt().plus(java.time.Duration.ofHours(24));}
 @Override public void delete(IdempotencyRecord record){repository.deleteById(record.id());}
}
