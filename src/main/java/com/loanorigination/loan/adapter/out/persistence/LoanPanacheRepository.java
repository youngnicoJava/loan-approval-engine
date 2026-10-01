package com.loanorigination.loan.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
@ApplicationScoped public class LoanPanacheRepository implements PanacheRepositoryBase<LoanEntity, UUID> {
    public Optional<LoanEntity> findForOffer(UUID id){return find("loanOfferId",id).firstResultOptional();}
    public List<LoanEntity> findForCustomer(UUID id){return find("customerId",id).list();}
    public Optional<LoanEntity> findLocked(UUID id){return Optional.ofNullable(findById(id,LockModeType.PESSIMISTIC_WRITE));}
}
