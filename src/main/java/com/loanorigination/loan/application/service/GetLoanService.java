package com.loanorigination.loan.application.service;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.loan.application.exception.LoanAccessDeniedException;
import com.loanorigination.loan.application.exception.LoanNotFoundException;
import com.loanorigination.loan.application.port.in.GetLoanUseCase;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.Loan;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.security.domain.ApplicationRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
@ApplicationScoped public class GetLoanService implements GetLoanUseCase {
    private final LoanRepository loans; private final CustomerRepository customers; private final CurrentUserPort currentUser;
    @Inject public GetLoanService(LoanRepository loans,CustomerRepository customers,CurrentUserPort currentUser){this.loans=loans;this.customers=customers;this.currentUser=currentUser;}
    public Loan get(UUID id){Loan loan=loans.findById(id).orElseThrow(()->new LoanNotFoundException(id)); var user=currentUser.getCurrentUser(); if(user.hasRole(ApplicationRole.CUSTOMER)){String sub=user.externalIdentityId(); var customer=customers.findByExternalIdentityId(sub).orElseThrow(()->new CustomerNotFoundException(sub)); if(!loan.customerId().equals(customer.id())) throw new LoanAccessDeniedException(id);} return loan;}
}
