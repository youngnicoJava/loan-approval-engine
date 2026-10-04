package com.loanorigination.loan.adapter.out.persistence;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.Loan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class LoanPersistenceAdapter implements LoanRepository {
    private final LoanPanacheRepository repo; @Inject public LoanPersistenceAdapter(LoanPanacheRepository repo){this.repo=repo;}
    public void save(Loan l){var e=repo.findByIdOptional(l.id()).orElse(null);if(e==null)repo.persist(LoanEntity.fromDomain(l));else e.update(l);}
    public Optional<Loan> findById(UUID id){return repo.findByIdOptional(id).map(LoanEntity::toDomain);}
    public Optional<Loan> findByIdForUpdate(UUID id){return repo.findLocked(id).map(LoanEntity::toDomain);}
    public Optional<Loan> findByOfferId(UUID id){return repo.findForOffer(id).map(LoanEntity::toDomain);}
    public List<Loan> findByCustomerId(UUID id){return repo.findForCustomer(id).stream().map(LoanEntity::toDomain).toList();}
    public List<Loan> findForOperations(com.loanorigination.loan.domain.LoanStatus status,int limit){return repo.findForOperations(status,limit).stream().map(LoanEntity::toDomain).toList();}
}
