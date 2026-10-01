package com.loanorigination.audit.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AuditPanacheRepository implements PanacheRepositoryBase<AuditEventEntity, UUID> {
    public List<AuditEventEntity> latest(int offset,int limit){return find("order by occurredAt desc").range(offset,offset+limit-1).list();}
    public List<AuditEventEntity> byAggregate(UUID id,int offset,int limit){return find("aggregateId = ?1 order by occurredAt desc",id).range(offset,offset+limit-1).list();}
}
