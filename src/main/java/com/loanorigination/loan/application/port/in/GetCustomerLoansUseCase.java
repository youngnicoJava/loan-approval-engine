package com.loanorigination.loan.application.port.in;
import com.loanorigination.loan.domain.Loan;
import java.util.List;
import java.util.UUID;
public interface GetCustomerLoansUseCase { List<Loan> getForCurrentCustomer(); List<Loan> getForCustomer(UUID customerId); }
