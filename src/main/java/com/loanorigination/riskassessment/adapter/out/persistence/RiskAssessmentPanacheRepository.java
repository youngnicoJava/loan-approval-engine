package com.loanorigination.riskassessment.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio técnico de Panache.
 *
 * Está separado del puerto de aplicación para mantener la dependencia
 * de Hibernate fuera de application/domain.
 */
@ApplicationScoped
public class RiskAssessmentPanacheRepository
        implements PanacheRepositoryBase<RiskAssessmentEntity, UUID> {

    public Optional<RiskAssessmentEntity>
    findLatestByLoanApplicationId(
            UUID loanApplicationId
    ) {

        return find(
                "loanApplicationId = ?1 ORDER BY assessedAt DESC",
                loanApplicationId
        ).firstResultOptional();
    }
}