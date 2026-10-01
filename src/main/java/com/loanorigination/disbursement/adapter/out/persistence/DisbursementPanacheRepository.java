package com.loanorigination.disbursement.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class DisbursementPanacheRepository implements PanacheRepositoryBase<DisbursementEntity,UUID>{
    public Optional<DisbursementEntity> findForLoan(UUID id){return find("loanId",id).firstResultOptional();}
    public Optional<DisbursementEntity> findForLoanLocked(UUID id){return find("loanId",id).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional();}
}
