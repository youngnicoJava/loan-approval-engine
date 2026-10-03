package com.loanorigination.riskassessment.adapter.out.persistence;

import com.loanorigination.riskassessment.application.port.out.RiskAssessmentRepository;
import com.loanorigination.riskassessment.domain.RiskAssessment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RiskAssessmentPersistenceAdapter
        implements RiskAssessmentRepository {

    private final RiskAssessmentPanacheRepository repository;

    @Inject
    public RiskAssessmentPersistenceAdapter(
            RiskAssessmentPanacheRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void save(RiskAssessment riskAssessment) {

        repository.persist(
                RiskAssessmentEntity.fromDomain(
                        riskAssessment
                )
        );
    }

    @Override
    public Optional<RiskAssessment> findById(UUID assessmentId) {
        return repository.findByIdOptional(assessmentId).map(RiskAssessmentEntity::toDomain);
    }

    @Override
    public Optional<RiskAssessment>
    findLatestByLoanApplicationId(
            UUID loanApplicationId
    ) {

        return repository
                .findLatestByLoanApplicationId(
                        loanApplicationId
                )
                .map(RiskAssessmentEntity::toDomain);
    }
}
