package com.loanorigination.audit.application.port.out;

import com.loanorigination.audit.domain.AuditEvent;
import java.util.List;
import java.util.UUID;

public interface AuditRepository {
    void append(AuditEvent event);
    List<AuditEvent> findAll(int offset, int limit);
    List<AuditEvent> findByAggregateId(UUID aggregateId, int offset, int limit);
}
