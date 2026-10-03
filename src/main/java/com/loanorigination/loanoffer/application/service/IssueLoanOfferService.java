package com.loanorigination.loanoffer.application.service;
import com.loanorigination.customer.application.exception.CustomerBlockedException;
import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.customer.domain.CustomerStatus;
import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.loanoffer.application.exception.LoanOfferAlreadyExistsException;
import com.loanorigination.loanoffer.application.exception.LoanApplicationNotApprovedException;
import com.loanorigination.loanoffer.application.exception.InvalidLoanOfferTermsException;
import com.loanorigination.loanoffer.application.port.in.IssueLoanOfferUseCase;
import com.loanorigination.loanoffer.application.port.out.LoanOfferRepository;
import com.loanorigination.repayment.application.port.out.AmortizationCalculatorPort;
import com.loanorigination.loanoffer.domain.LoanOffer;
import com.loanorigination.loanoffer.domain.LoanOfferStatus;
import com.loanorigination.shared.domain.InterestRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.time.Clock;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;

@ApplicationScoped
public class IssueLoanOfferService implements IssueLoanOfferUseCase {
    private final LoanApplicationRepository applications; private final CustomerRepository customers; private final LoanOfferRepository offers; private final Clock clock; private final AmortizationCalculatorPort amortizationCalculator; private final WorkflowEventRecorder events;
    private final com.loanorigination.fraudassessment.application.FraudAssessmentGateService fraudGate;
    @Inject public IssueLoanOfferService(LoanApplicationRepository applications, CustomerRepository customers, LoanOfferRepository offers, Clock clock, AmortizationCalculatorPort amortizationCalculator, WorkflowEventRecorder events, com.loanorigination.fraudassessment.application.FraudAssessmentGateService fraudGate) {
        this.applications=applications; this.customers=customers; this.offers=offers; this.clock=clock; this.amortizationCalculator=amortizationCalculator; this.events=events; this.fraudGate=fraudGate;
    }
    @Override @Transactional public LoanOffer issue(UUID applicationId, BigDecimal offeredPrincipal, Integer offeredTermMonths, BigDecimal annualRatePercentage, Instant expiresAt) {
        LoanApplication app=applications.findByIdForUpdate(applicationId).orElseThrow(()->new LoanApplicationNotFoundException(applicationId));
        if (app.status()!=LoanApplicationStatus.APPROVED) throw new LoanApplicationNotApprovedException(applicationId);
        fraudGate.assertCleared(applicationId);
        Customer customer=customers.findById(app.customerId()).orElseThrow(()->new CustomerNotFoundException(app.customerId()));
        if (customer.status()==CustomerStatus.BLOCKED) throw new CustomerBlockedException(customer.id());
        Instant now=clock.instant();
        var existing=offers.findActiveByApplicationId(applicationId);
        if(existing.isPresent()) {
            LoanOffer old=existing.get();
            if(old.status()==LoanOfferStatus.PENDING && old.expireIfDue(now)) offers.save(old);
            else throw new LoanOfferAlreadyExistsException(applicationId);
        }
        InterestRate rate=InterestRate.ofPercentage(annualRatePercentage);
        BigDecimal amount=offeredPrincipal==null?app.requestedAmount().amount():offeredPrincipal;
        int months=offeredTermMonths==null?app.term().months():offeredTermMonths;
        if(amount.signum()<=0 || amount.compareTo(app.requestedAmount().amount())>0) throw new InvalidLoanOfferTermsException("Offered principal must be positive and no greater than the requested amount");
        if(months<=0 || months>app.term().months()) throw new InvalidLoanOfferTermsException("Offered term must be positive and no greater than the requested term");
        Money principal=Money.of(amount,app.requestedAmount().currency());
        LoanTerm term=LoanTerm.ofMonths(months);
        var quote=amortizationCalculator.calculateQuote(principal, rate, term);
        if (expiresAt==null || !expiresAt.isAfter(now)) throw new InvalidLoanOfferTermsException("expiresAt must be in the future");
        LoanOffer offer=LoanOffer.create(app.id(), customer.id(), principal, term, rate,
                quote.regularInstallment(), quote.totalRepayment(), now, expiresAt);
        offers.save(offer);
        events.record("LoanOfferCreated", AuditAction.OFFER_CREATED, "LoanOffer", offer.id(),
                java.util.Map.of("loanOfferId",offer.id().toString(),"loanApplicationId",app.id().toString(),"customerId",customer.id().toString()));
        return offer;
    }
}
