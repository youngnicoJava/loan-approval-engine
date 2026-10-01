package com.loanorigination.repayment.application.port.in;
import com.loanorigination.repayment.domain.Installment;
import java.util.List;
import java.util.UUID;
public interface GetInstallmentsUseCase { List<Installment> getForLoan(UUID loanId); }
