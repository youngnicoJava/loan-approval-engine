package com.loanorigination.fraudassessment.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FraudAssessmentPanacheRepository implements PanacheRepositoryBase<FraudAssessmentEntity,UUID> {
  public Optional<FraudAssessmentEntity> latest(UUID applicationId) {
    return find("loanApplicationId = ?1 order by evaluatedAt desc",applicationId).firstResultOptional();
  }
}
