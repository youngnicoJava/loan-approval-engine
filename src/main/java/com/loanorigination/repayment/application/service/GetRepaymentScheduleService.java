package com.loanorigination.repayment.application.service;
import com.loanorigination.loan.application.port.in.GetLoanUseCase;
import com.loanorigination.loan.domain.LoanStatus;
import com.loanorigination.repayment.application.exception.RepaymentScheduleNotFoundException;
import com.loanorigination.repayment.application.port.in.GetRepaymentScheduleUseCase;
import com.loanorigination.repayment.application.port.out.RepaymentScheduleRepository;
import com.loanorigination.repayment.domain.RepaymentSchedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
@ApplicationScoped public class GetRepaymentScheduleService implements GetRepaymentScheduleUseCase{
    private final GetLoanUseCase loans;private final RepaymentScheduleRepository schedules;
    @Inject public GetRepaymentScheduleService(GetLoanUseCase loans,RepaymentScheduleRepository schedules){this.loans=loans;this.schedules=schedules;}
    public RepaymentSchedule getForLoan(UUID id){var loan=loans.get(id);if(loan.status()!=LoanStatus.ACTIVE)throw new RepaymentScheduleNotFoundException(id);return schedules.findByLoanId(id).orElseThrow(()->new RepaymentScheduleNotFoundException(id));}
}
