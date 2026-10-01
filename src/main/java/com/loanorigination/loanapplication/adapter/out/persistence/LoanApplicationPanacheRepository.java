package com.loanorigination.loanapplication.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

/**
 * Repositorio técnico de Panache.
 *
 * Este componente conoce exclusivamente la forma en que Hibernate/Panache
 * accede a PostgreSQL. No forma parte del contrato de aplicación.
 */
@ApplicationScoped
public class LoanApplicationPanacheRepository
        implements PanacheRepositoryBase<LoanApplicationEntity, UUID> {
}