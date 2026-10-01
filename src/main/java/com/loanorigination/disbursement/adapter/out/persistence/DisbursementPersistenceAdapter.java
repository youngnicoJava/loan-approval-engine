package com.loanorigination.disbursement.adapter.out.persistence;
import com.loanorigination.disbursement.application.port.out.DisbursementRepository;
import com.loanorigination.disbursement.domain.Disbursement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class DisbursementPersistenceAdapter implements DisbursementRepository{
    private final DisbursementPanacheRepository repo;@Inject public DisbursementPersistenceAdapter(DisbursementPanacheRepository repo){this.repo=repo;}
    public void save(Disbursement d){var entity=repo.findByIdOptional(d.id()).orElse(null);if(entity==null)repo.persist(DisbursementEntity.fromDomain(d));else entity.update(d);}
    public Optional<Disbursement> findByLoanId(UUID id){return repo.findForLoan(id).map(DisbursementEntity::toDomain);}
    public Optional<Disbursement> findByLoanIdForUpdate(UUID id){return repo.findForLoanLocked(id).map(DisbursementEntity::toDomain);}
    public Optional<Disbursement> findById(UUID id){return repo.findByIdOptional(id).map(DisbursementEntity::toDomain);}
}
