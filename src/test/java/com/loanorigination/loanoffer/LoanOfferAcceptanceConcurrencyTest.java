package com.loanorigination.loanoffer;

import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.loanoffer.application.port.in.AcceptLoanOfferUseCase;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.security.adapter.oidc.OidcCurrentUserAdapter;
import com.loanorigination.security.domain.AuthenticatedUser;
import com.loanorigination.shared.domain.InterestRate;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusMock;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class LoanOfferAcceptanceConcurrencyTest {
    @Inject CustomerRepository customers;
    @Inject LoanApplicationRepository applications;
    @Inject LoanOfferRepository offers;
    @Inject LoanRepository loans;
    @Inject AcceptLoanOfferUseCase accept;
    @Inject EntityManager entityManager;
    @Inject UserTransaction transaction;

    @Test
    void simultaneousAcceptsCreateOnlyOneLoan() throws Exception {
        String identity="accept-race-"+UUID.randomUUID();
        QuarkusMock.installMockForType(new TestCurrentUserAdapter(identity),OidcCurrentUserAdapter.class);
        Instant now=Instant.now();
        Customer customer=Customer.create(identity,"Concurrency Test","accept-race@example.invalid",now);
        var money=Money.of(new BigDecimal("10000.00"),Currency.getInstance("ARS"));
        var term=LoanTerm.ofMonths(12);
        LoanApplication application=LoanApplication.restore(UUID.randomUUID(),customer.id(),LoanProductType.PERSONAL_LOAN,
                money,term,"concurrent acceptance",LoanApplicationStatus.APPROVED,now,now,now);
        LoanOffer offer=LoanOffer.create(application.id(),customer.id(),money,term,InterestRate.ofPercentage(new BigDecimal("12.0000")),
                new BigDecimal("888.89"),new BigDecimal("10666.68"),now,now.plusSeconds(3600));
        transaction.begin();
        customers.save(customer);applications.save(application);offers.save(offer);
        transaction.commit();

        var start=new CountDownLatch(1);
        var executor=Executors.newFixedThreadPool(2);
        try {
            var first=executor.submit(()->{start.await();return accept.accept(offer.id());});
            var second=executor.submit(()->{start.await();return accept.accept(offer.id());});
            start.countDown();
            var firstLoan=first.get(20,TimeUnit.SECONDS);
            var secondLoan=second.get(20,TimeUnit.SECONDS);
            assertEquals(firstLoan.id(),secondLoan.id());
            assertEquals(1,countLoansFor(offer.id()));
            assertEquals(1,loans.findByOfferId(offer.id()).stream().count());
        } finally {
            executor.shutdownNow();
        }
    }

    private long countLoansFor(UUID offerId) throws Exception {
        transaction.begin();
        try {long count=((Number)entityManager.createNativeQuery("select count(*) from loans where loan_offer_id=:offer")
                .setParameter("offer",offerId).getSingleResult()).longValue();transaction.commit();return count;}
        catch(Exception e){transaction.rollback();throw e;}
    }

    static class TestCurrentUserAdapter extends OidcCurrentUserAdapter {
        private final AuthenticatedUser user;
        TestCurrentUserAdapter(String identity){super(null,null);this.user=new AuthenticatedUser(identity,Set.of("CUSTOMER"));}
        @Override public AuthenticatedUser getCurrentUser(){return user;}
    }
}
