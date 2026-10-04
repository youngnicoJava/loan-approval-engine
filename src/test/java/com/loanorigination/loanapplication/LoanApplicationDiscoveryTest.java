package com.loanorigination.loanapplication;

import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.loanapplication.domain.ApplicantFinancialProfile;
import com.loanorigination.loanapplication.domain.ApplicantEmploymentStatus;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LoanApplicationDiscoveryTest {
    @Inject UserTransaction transaction;
    @Inject CustomerRepository customers;
    @Inject LoanApplicationRepository applications;
    @Inject EntityManager entityManager;

    @Test
    void queriesReturnCustomerOwnedAndStateFilteredApplications() throws Exception {
        UUID customerId;
        UUID submittedId;
        transaction.begin();
        try {
            var customer = Customer.create("discovery-" + UUID.randomUUID(), "Discovery Test", "discovery@example.invalid", Instant.now());
            customers.save(customer);
            customerId = customer.id();
            var submitted = application(customerId);
            submitted.submit(Instant.now());
            applications.save(submitted);
            submittedId = submitted.id();
            var draft = application(customerId);
            applications.save(draft);
            UUID draftId = draft.id();
            entityManager.flush();

            assertEquals(2, applications.findByCustomerId(customerId).size());
            var submittedQueue = applications.findForQueue(LoanApplicationStatus.SUBMITTED, 0, 1_000_000);
            assertEquals(1, submittedQueue.stream().filter(item -> item.id().equals(submittedId)).count());
            assertEquals(false, submittedQueue.stream().anyMatch(item -> item.customerId().equals(customerId)
                    && item.status() == LoanApplicationStatus.DRAFT));
            var queue = applications.findForQueue(null, 0, 1_000_000);
            assertEquals(true, queue.stream().anyMatch(item -> item.id().equals(submittedId)));
            assertEquals(draftId, applications.findForQueue(LoanApplicationStatus.DRAFT, 0, 25).getFirst().id());
        } finally {
            transaction.rollback();
        }
    }

    private LoanApplication application(UUID customerId) {
        return LoanApplication.create(customerId, LoanProductType.PERSONAL_LOAN,
                Money.of(new BigDecimal("10000.00"), Currency.getInstance("ARS")), LoanTerm.ofMonths(12),
                "discovery-test", new ApplicantFinancialProfile(Money.of(new BigDecimal("250000.00"),Currency.getInstance("ARS")),Money.of(BigDecimal.ZERO,Currency.getInstance("ARS")),ApplicantEmploymentStatus.PERMANENT,24), Instant.now());
    }
}
