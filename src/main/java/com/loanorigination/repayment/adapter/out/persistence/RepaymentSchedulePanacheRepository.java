package com.loanorigination.repayment.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class RepaymentSchedulePanacheRepository implements PanacheRepositoryBase<RepaymentScheduleEntity,UUID>{
    public Optional<RepaymentScheduleEntity> findForLoan(UUID id){return find("loanId = ?1 order by createdAt desc",id).firstResultOptional();}
    public Optional<RepaymentScheduleEntity> findActiveForLoan(UUID id){return find("loanId = ?1 and status = 'ACTIVE'",id).firstResultOptional();}
}
