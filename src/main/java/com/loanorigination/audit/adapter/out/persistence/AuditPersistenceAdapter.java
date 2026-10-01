package com.loanorigination.audit.adapter.out.persistence;

import com.loanorigination.audit.application.port.out.AuditRepository;
import com.loanorigination.audit.domain.AuditEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AuditPersistenceAdapter implements AuditRepository {
    private final AuditPanacheRepository repository;
    @Inject public AuditPersistenceAdapter(AuditPanacheRepository repository){this.repository=repository;}
    @Override public void append(AuditEvent event){repository.persist(AuditEventEntity.from(event));}
    @Override public List<AuditEvent> findAll(int offset,int limit){return repository.latest(offset,limit).stream().map(AuditEventEntity::toDomain).toList();}
    @Override public List<AuditEvent> findByAggregateId(UUID id,int offset,int limit){return repository.byAggregate(id,offset,limit).stream().map(AuditEventEntity::toDomain).toList();}
}
