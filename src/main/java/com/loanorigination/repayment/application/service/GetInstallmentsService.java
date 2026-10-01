package com.loanorigination.repayment.application.service;
import com.loanorigination.loan.application.port.in.GetLoanUseCase;
import com.loanorigination.repayment.application.port.in.GetInstallmentsUseCase;
import com.loanorigination.repayment.application.port.in.GetRepaymentScheduleUseCase;
import com.loanorigination.repayment.domain.Installment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
@ApplicationScoped public class GetInstallmentsService implements GetInstallmentsUseCase{
    private final GetRepaymentScheduleUseCase schedules;private final GetLoanUseCase loans;
    @Inject public GetInstallmentsService(GetRepaymentScheduleUseCase schedules,GetLoanUseCase loans){this.schedules=schedules;this.loans=loans;}
    public List<Installment> getForLoan(UUID id){return schedules.getForLoan(id).installments();}
}
