package com.loanorigination.loanoffer.adapter.out.persistence;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.loanoffer.domain.LoanOffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class LoanOfferPersistenceAdapter implements LoanOfferRepository {
    private final LoanOfferPanacheRepository repo; @Inject public LoanOfferPersistenceAdapter(LoanOfferPanacheRepository repo){this.repo=repo;}
    public void save(LoanOffer o){var e=repo.findByIdOptional(o.id()).orElse(null);if(e==null)repo.persist(LoanOfferEntity.fromDomain(o));else e.update(o);}
    public Optional<LoanOffer> findById(UUID id){return repo.findByIdOptional(id).map(LoanOfferEntity::toDomain);}
    public Optional<LoanOffer> findByIdForUpdate(UUID id){return repo.findLocked(id).map(LoanOfferEntity::toDomain);}
    public Optional<LoanOffer> findActiveByApplicationId(UUID id){return repo.findByApplication(id).map(LoanOfferEntity::toDomain);}
    public List<LoanOffer> findByCustomerId(UUID id){return repo.findForCustomer(id).stream().map(LoanOfferEntity::toDomain).toList();}
}
