package com.loanorigination.repayment.application.port.out;
import com.loanorigination.repayment.domain.RepaymentSchedule;
import java.util.Optional;
import java.util.UUID;
public interface RepaymentScheduleRepository {
    void save(RepaymentSchedule schedule);
    Optional<RepaymentSchedule> findByLoanId(UUID loanId);
    Optional<RepaymentSchedule> findActiveByLoanId(UUID loanId);
}
