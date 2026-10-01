package com.loanorigination.loanoffer.application.service;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.loanoffer.application.exception.LoanOfferAccessDeniedException;
import com.loanorigination.loanoffer.application.exception.LoanOfferNotFoundException;
import com.loanorigination.loanoffer.application.port.in.DeclineLoanOfferUseCase;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import java.time.Clock;
@ApplicationScoped public class DeclineLoanOfferService implements DeclineLoanOfferUseCase {
    private final LoanOfferRepository offers; private final CustomerRepository customers; private final CurrentUserPort currentUser; private final Clock clock;
    @Inject public DeclineLoanOfferService(LoanOfferRepository offers, CustomerRepository customers, CurrentUserPort currentUser, Clock clock){this.offers=offers;this.customers=customers;this.currentUser=currentUser;this.clock=clock;}
    @Override @Transactional public LoanOffer decline(UUID id){
        LoanOffer offer=offers.findByIdForUpdate(id).orElseThrow(()->new LoanOfferNotFoundException(id));
        String sub=currentUser.getCurrentUser().externalIdentityId(); var customer=customers.findByExternalIdentityId(sub).orElseThrow(()->new CustomerNotFoundException(sub));
        if(!customer.id().equals(offer.customerId())) throw new LoanOfferAccessDeniedException(id);
        offer.decline(clock.instant()); offers.save(offer); return offer;
    }
}
