package com.loanorigination.loanapplication.application.service;

import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.loanapplication.application.port.in.ListLoanApplicationsUseCase;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListLoanApplicationsService implements ListLoanApplicationsUseCase {
    private final LoanApplicationRepository applications;
    private final CustomerRepository customers;
    private final CurrentUserPort currentUser;

    @Inject
    public ListLoanApplicationsService(LoanApplicationRepository applications, CustomerRepository customers, CurrentUserPort currentUser) {
        this.applications = applications;
        this.customers = customers;
        this.currentUser = currentUser;
    }

    @Override
    public List<LoanApplication> forCurrentCustomer() {
        String subject = currentUser.getCurrentUser().externalIdentityId();
        var customer = customers.findByExternalIdentityId(subject)
                .orElseThrow(() -> new CustomerNotFoundException(subject));
        return applications.findByCustomerId(customer.id());
    }

    @Override
    public List<LoanApplication> forOperations(LoanApplicationStatus status, int offset, int limit) {
        return applications.findForQueue(status, offset, limit);
    }
}
