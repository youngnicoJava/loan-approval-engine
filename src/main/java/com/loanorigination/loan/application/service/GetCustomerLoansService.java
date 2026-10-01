package com.loanorigination.loan.application.service;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.loan.application.port.in.GetCustomerLoansUseCase;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.Loan;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
@ApplicationScoped public class GetCustomerLoansService implements GetCustomerLoansUseCase {
    private final LoanRepository loans; private final CustomerRepository customers; private final CurrentUserPort currentUser;
    @Inject public GetCustomerLoansService(LoanRepository loans,CustomerRepository customers,CurrentUserPort currentUser){this.loans=loans;this.customers=customers;this.currentUser=currentUser;}
    public List<Loan> getForCurrentCustomer(){String sub=currentUser.getCurrentUser().externalIdentityId(); var customer=customers.findByExternalIdentityId(sub).orElseThrow(()->new CustomerNotFoundException(sub));return loans.findByCustomerId(customer.id());}
    public List<Loan> getForCustomer(UUID id){return loans.findByCustomerId(id);}
}
