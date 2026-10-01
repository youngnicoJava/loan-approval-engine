package com.loanorigination.loanoffer.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class LoanOfferPanacheRepository implements PanacheRepositoryBase<LoanOfferEntity, UUID> {
    public Optional<LoanOfferEntity> findByApplication(UUID id){return find("loanApplicationId = ?1 and status in ('PENDING','ACCEPTED')",id).firstResultOptional();}
    public List<LoanOfferEntity> findForCustomer(UUID id){return find("customerId",id).list();}
    public Optional<LoanOfferEntity> findLocked(UUID id){return Optional.ofNullable(findById(id,LockModeType.PESSIMISTIC_WRITE));}
}
