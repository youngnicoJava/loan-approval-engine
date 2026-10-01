package com.loanorigination.idempotency.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class IdempotencyPanacheRepository implements PanacheRepositoryBase<IdempotencyEntity,UUID> {
 public Optional<IdempotencyEntity> lock(String actor,String scope,String key){return find("actorId = ?1 and scope = ?2 and idempotencyKey = ?3",actor,scope,key).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional();}
}
