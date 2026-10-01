package com.loanorigination.loanoffer.application.port.in;
import com.loanorigination.loanoffer.domain.LoanOffer;
import java.util.List;
import java.util.UUID;
public interface GetCustomerLoanOffersUseCase { List<LoanOffer> getForCurrentCustomer(); List<LoanOffer> getForCustomer(UUID customerId); }
