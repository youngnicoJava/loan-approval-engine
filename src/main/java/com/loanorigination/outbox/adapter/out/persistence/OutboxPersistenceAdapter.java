package com.loanorigination.outbox.adapter.out.persistence;

import com.loanorigination.outbox.application.port.out.OutboxRepository;
import com.loanorigination.outbox.domain.OutboxEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxPersistenceAdapter implements OutboxRepository {
    private final OutboxPanacheRepository repository;
    private final EntityManager entityManager;
    @Inject public OutboxPersistenceAdapter(OutboxPanacheRepository repository,EntityManager entityManager){this.repository=repository;this.entityManager=entityManager;}
    @Override public void append(OutboxEvent event){repository.persist(OutboxEventEntity.from(event));}
    @Override @Transactional public List<UUID> claimBatch(Instant now,Instant expiredLease,int limit){
        @SuppressWarnings("unchecked")
        List<UUID> ids=entityManager.createNativeQuery("UPDATE outbox_events SET status='PROCESSING', locked_at=:now, attempt_count=attempt_count+1 WHERE id IN (SELECT id FROM outbox_events WHERE status IN ('PENDING','FAILED') OR (status='PROCESSING' AND locked_at < :lease) ORDER BY occurred_at LIMIT :limit FOR UPDATE SKIP LOCKED) RETURNING id")
                .setParameter("now",now).setParameter("lease",expiredLease).setParameter("limit",limit).getResultList();
        return ids;
    }
    @Override public OutboxEvent findById(UUID id){return repository.findByIdOptional(id).map(OutboxEventEntity::toDomain).orElseThrow();}
    @Override @Transactional public void markPublished(UUID id,Instant at){var e=repository.findById(id);if(e!=null){e.status=com.loanorigination.outbox.domain.OutboxStatus.PUBLISHED;e.publishedAt=at;e.lockedAt=null;e.lastError=null;}}
    @Override @Transactional public void markFailed(UUID id,String reason){var e=repository.findById(id);if(e!=null){e.status=com.loanorigination.outbox.domain.OutboxStatus.FAILED;e.lockedAt=null;e.lastError=reason==null?"Publisher failure":reason.substring(0,Math.min(1000,reason.length()));}}
}
