package com.loanorigination.loanoffer.application.port.out;
import com.loanorigination.loanoffer.domain.LoanOffer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface LoanOfferRepository {
    void save(LoanOffer offer);
    Optional<LoanOffer> findById(UUID id);
    Optional<LoanOffer> findByIdForUpdate(UUID id);
    Optional<LoanOffer> findActiveByApplicationId(UUID applicationId);
    List<LoanOffer> findByCustomerId(UUID customerId);
}
