package com.loanorigination.loanoffer.application.service;
import com.loanorigination.customer.application.exception.CustomerBlockedException;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.CustomerStatus;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.Loan;
import com.loanorigination.loanoffer.application.exception.LoanOfferAccessDeniedException;
import com.loanorigination.loanoffer.application.exception.LoanOfferNotFoundException;
import com.loanorigination.loanoffer.application.port.in.AcceptLoanOfferUseCase;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.loanoffer.domain.LoanOfferExpiredException;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import java.time.Clock;

@ApplicationScoped
public class AcceptLoanOfferService implements AcceptLoanOfferUseCase {
    private final LoanOfferRepository offers; private final LoanRepository loans; private final CustomerRepository customers; private final CurrentUserPort currentUser; private final Clock clock;
    @Inject public AcceptLoanOfferService(LoanOfferRepository offers, LoanRepository loans, CustomerRepository customers, CurrentUserPort currentUser, Clock clock) {
        this.offers=offers; this.loans=loans; this.customers=customers; this.currentUser=currentUser; this.clock=clock;
    }
    @Override @Transactional(dontRollbackOn=LoanOfferExpiredException.class) public Loan accept(UUID offerId) {
        LoanOffer offer=offers.findByIdForUpdate(offerId).orElseThrow(()->new LoanOfferNotFoundException(offerId));
        var customer=customers.findByExternalIdentityId(currentUser.getCurrentUser().externalIdentityId())
                .orElseThrow(()->new CustomerNotFoundException(currentUser.getCurrentUser().externalIdentityId()));
        if (!customer.id().equals(offer.customerId())) throw new LoanOfferAccessDeniedException(offerId);
        if (offer.status()==com.loanorigination.loanoffer.domain.LoanOfferStatus.ACCEPTED) {
            return loans.findByOfferId(offerId).orElseThrow(()->new IllegalStateException("Accepted offer has no associated loan"));
        }
        if (customer.status()==CustomerStatus.BLOCKED) throw new CustomerBlockedException(customer.id());
        Instant now=clock.instant();
        if (!now.isBefore(offer.expiresAt())) {
            offer.expireIfDue(now); offers.save(offer); throw new LoanOfferExpiredException(offerId);
        }
        offer.accept(now); offers.save(offer);
        Loan loan=Loan.createFromAcceptedOffer(offer.id(), offer.loanApplicationId(), offer.customerId(), offer.principal(),
                offer.term(), offer.annualInterestRate(), offer.monthlyInstallment(), offer.totalRepayment(), now);
        loans.save(loan);
        // Punto futuro de publicación de LOAN_CREATED mediante outbox.
        return loan;
    }
}
