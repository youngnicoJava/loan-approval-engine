package com.loanorigination.loanoffer.application.service;
import com.loanorigination.loanoffer.application.exception.LoanOfferNotFoundException;
import com.loanorigination.loanoffer.application.port.in.GetLoanOfferUseCase;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.security.domain.ApplicationRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import jakarta.transaction.Transactional;
import java.time.Clock;
@ApplicationScoped public class GetLoanOfferService implements GetLoanOfferUseCase {
    private final LoanOfferRepository offers; private final CustomerRepository customers; private final CurrentUserPort currentUser; private final Clock clock;
    @Inject public GetLoanOfferService(LoanOfferRepository offers,CustomerRepository customers,CurrentUserPort currentUser,Clock clock){this.offers=offers;this.customers=customers;this.currentUser=currentUser;this.clock=clock;}
    @Transactional public LoanOffer get(UUID id){var offer=offers.findById(id).orElseThrow(()->new LoanOfferNotFoundException(id)); var user=currentUser.getCurrentUser(); if(user.hasRole(ApplicationRole.CUSTOMER)){var customer=customers.findByExternalIdentityId(user.externalIdentityId()).orElseThrow(()->new CustomerNotFoundException(user.externalIdentityId()));if(!offer.customerId().equals(customer.id()))throw new com.loanorigination.loanoffer.application.exception.LoanOfferAccessDeniedException(id);} if(offer.expireIfDue(clock.instant()))offers.save(offer); return offer;}
}
