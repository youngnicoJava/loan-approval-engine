package com.loanorigination.loanapplication.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;
import java.util.List;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;

/**
 * Repositorio técnico de Panache.
 *
 * Este componente conoce exclusivamente la forma en que Hibernate/Panache
 * accede a PostgreSQL. No forma parte del contrato de aplicación.
 */
@ApplicationScoped
public class LoanApplicationPanacheRepository
        implements PanacheRepositoryBase<LoanApplicationEntity, UUID> {

    public List<LoanApplicationEntity> findByCustomer(UUID customerId) {
        return find("customerId = ?1 order by createdAt desc", customerId).list();
    }

    public List<LoanApplicationEntity> findQueue(LoanApplicationStatus status, int offset, int limit) {
        if (status != null) {
            return find("status = ?1 order by updatedAt desc", status).range(offset, offset + limit - 1).list();
        }
        return find("order by updatedAt desc").range(offset, offset + limit - 1).list();
    }
}
