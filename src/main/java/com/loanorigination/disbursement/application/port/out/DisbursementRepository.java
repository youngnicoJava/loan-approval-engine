package com.loanorigination.disbursement.application.port.out;
import com.loanorigination.disbursement.domain.Disbursement;
import java.util.Optional;
import java.util.UUID;
public interface DisbursementRepository {
    void save(Disbursement disbursement);
    Optional<Disbursement> findByLoanId(UUID loanId);
    Optional<Disbursement> findByLoanIdForUpdate(UUID loanId);
    Optional<Disbursement> findById(UUID id);
}
