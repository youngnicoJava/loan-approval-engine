package com.loanorigination.loanoffer.application.service;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.loanoffer.application.port.in.GetCustomerLoanOffersUseCase;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import jakarta.transaction.Transactional;
import java.time.Clock;
@ApplicationScoped public class GetCustomerLoanOffersService implements GetCustomerLoanOffersUseCase {
    private final LoanOfferRepository offers; private final CustomerRepository customers; private final CurrentUserPort currentUser; private final Clock clock;
    @Inject public GetCustomerLoanOffersService(LoanOfferRepository offers, CustomerRepository customers, CurrentUserPort currentUser, Clock clock){this.offers=offers;this.customers=customers;this.currentUser=currentUser;this.clock=clock;}
    @Transactional public List<LoanOffer> getForCurrentCustomer(){String sub=currentUser.getCurrentUser().externalIdentityId(); var customer=customers.findByExternalIdentityId(sub).orElseThrow(()->new CustomerNotFoundException(sub)); return expire(offers.findByCustomerId(customer.id()));}
    @Transactional public List<LoanOffer> getForCustomer(UUID customerId){return expire(offers.findByCustomerId(customerId));}
    private List<LoanOffer> expire(List<LoanOffer> results){var now=clock.instant(); for(var offer:results)if(offer.expireIfDue(now))offers.save(offer); return results;}
}
