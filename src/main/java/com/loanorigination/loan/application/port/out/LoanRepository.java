package com.loanorigination.loan.application.port.out;
import com.loanorigination.loan.domain.Loan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface LoanRepository {
    void save(Loan loan);
    Optional<Loan> findById(UUID id);
    Optional<Loan> findByIdForUpdate(UUID id);
    Optional<Loan> findByOfferId(UUID offerId);
    List<Loan> findByCustomerId(UUID customerId);
    List<Loan> findForOperations(com.loanorigination.loan.domain.LoanStatus status, int limit);
}
