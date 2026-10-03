package com.loanorigination.fraudassessment.application;

import com.loanorigination.fraudassessment.domain.FraudCaseResolutionRecord;
import java.util.Optional;
import java.util.UUID;

public interface FraudCaseResolutionRepository {
    Optional<FraudCaseResolutionRecord> findLatestByLoanApplicationId(UUID loanApplicationId);
    Optional<FraudCaseResolutionRecord> findByFraudCaseId(UUID fraudCaseId);
    void save(FraudCaseResolutionRecord resolution);
}
