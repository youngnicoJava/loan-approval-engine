package com.loanorigination.repayment.adapter.out.persistence;
import com.loanorigination.repayment.application.port.out.RepaymentScheduleRepository;
import com.loanorigination.repayment.domain.RepaymentSchedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class RepaymentSchedulePersistenceAdapter implements RepaymentScheduleRepository{
    private final RepaymentSchedulePanacheRepository schedules;private final InstallmentPanacheRepository installments;
    @Inject public RepaymentSchedulePersistenceAdapter(RepaymentSchedulePanacheRepository schedules,InstallmentPanacheRepository installments){this.schedules=schedules;this.installments=installments;}
    public void save(RepaymentSchedule schedule){
        var entity=schedules.findByIdOptional(schedule.id()).orElse(null);
        if(entity==null)schedules.persist(RepaymentScheduleEntity.fromDomain(schedule));else entity.update(schedule);
        for(var item:schedule.installments()){
            var row=installments.findByIdOptional(item.id()).orElse(null);
            if(row==null)installments.persist(InstallmentEntity.fromDomain(schedule.id(),item));else row.update(item);
        }
    }
    public Optional<RepaymentSchedule> findByLoanId(UUID loanId){return schedules.findForLoan(loanId).map(this::toDomain);}
    public Optional<RepaymentSchedule> findActiveByLoanId(UUID loanId){return schedules.findActiveForLoan(loanId).map(this::toDomain);}
    private RepaymentSchedule toDomain(RepaymentScheduleEntity entity){return entity.toDomain(installments.findForSchedule(entity.id).stream().map(InstallmentEntity::toDomain).toList());}
}
