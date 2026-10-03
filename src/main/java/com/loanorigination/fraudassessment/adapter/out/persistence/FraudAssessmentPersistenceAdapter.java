package com.loanorigination.fraudassessment.adapter.out.persistence;

import com.loanorigination.fraudassessment.application.FraudAssessmentRepository;
import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FraudAssessmentPersistenceAdapter implements FraudAssessmentRepository {
  private final FraudAssessmentPanacheRepository repository;
  @Inject public FraudAssessmentPersistenceAdapter(FraudAssessmentPanacheRepository repository) { this.repository=repository; }
  public void save(FraudAssessmentResult result) { repository.persist(FraudAssessmentEntity.from(result)); }
  public Optional<FraudAssessmentResult> findByAssessmentId(UUID id) { return repository.findByIdOptional(id).map(FraudAssessmentEntity::toDomain); }
  public Optional<FraudAssessmentResult> findLatestByApplicationId(UUID id) { return repository.latest(id).map(FraudAssessmentEntity::toDomain); }
}
