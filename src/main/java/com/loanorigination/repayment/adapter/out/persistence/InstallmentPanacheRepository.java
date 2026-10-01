package com.loanorigination.repayment.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
@ApplicationScoped public class InstallmentPanacheRepository implements PanacheRepositoryBase<InstallmentEntity,UUID>{
    public List<InstallmentEntity> findForSchedule(UUID id){return find("scheduleId = ?1 order by installmentNumber",id).list();}
}
