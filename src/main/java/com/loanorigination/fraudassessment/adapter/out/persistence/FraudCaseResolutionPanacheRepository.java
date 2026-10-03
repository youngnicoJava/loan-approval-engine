package com.loanorigination.fraudassessment.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FraudCaseResolutionPanacheRepository
        implements PanacheRepositoryBase<FraudCaseResolutionEntity, UUID> {
    public Optional<FraudCaseResolutionEntity> latestForApplication(UUID applicationId) {
        return find("loanApplicationId = ?1 order by resolvedAt desc, fraudCaseId desc", applicationId)
                .firstResultOptional();
    }
}
