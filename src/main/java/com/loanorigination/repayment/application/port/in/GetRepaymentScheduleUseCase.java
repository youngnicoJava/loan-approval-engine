package com.loanorigination.repayment.application.port.in;
import com.loanorigination.repayment.domain.RepaymentSchedule;
import java.util.UUID;
public interface GetRepaymentScheduleUseCase { RepaymentSchedule getForLoan(UUID loanId); }
