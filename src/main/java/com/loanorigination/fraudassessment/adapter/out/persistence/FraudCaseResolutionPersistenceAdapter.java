package com.loanorigination.fraudassessment.adapter.out.persistence;

import com.loanorigination.fraudassessment.application.FraudCaseResolutionRepository;
import com.loanorigination.fraudassessment.domain.FraudCaseResolutionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FraudCaseResolutionPersistenceAdapter implements FraudCaseResolutionRepository {
    private final FraudCaseResolutionPanacheRepository repository;

    @Inject
    public FraudCaseResolutionPersistenceAdapter(FraudCaseResolutionPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<FraudCaseResolutionRecord> findLatestByLoanApplicationId(UUID loanApplicationId) {
        return repository.latestForApplication(loanApplicationId).map(FraudCaseResolutionEntity::toDomain);
    }

    @Override
    public Optional<FraudCaseResolutionRecord> findByFraudCaseId(UUID fraudCaseId) {
        return repository.findByIdOptional(fraudCaseId).map(FraudCaseResolutionEntity::toDomain);
    }

    @Override
    @Transactional
    public void save(FraudCaseResolutionRecord resolution) {
        repository.persist(FraudCaseResolutionEntity.from(resolution));
    }
}
