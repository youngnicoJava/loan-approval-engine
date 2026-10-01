package com.loanorigination.disbursement.application.service;
import com.loanorigination.disbursement.application.exception.LoanNotEligibleForDisbursementException;
import com.loanorigination.disbursement.application.port.out.DisbursementRepository;
import com.loanorigination.disbursement.domain.Disbursement;
import com.loanorigination.disbursement.domain.DisbursementStatus;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.LoanStatus;
import com.loanorigination.repayment.application.port.out.RepaymentScheduleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.UUID;
@ApplicationScoped public class PrepareDisbursementService{
    private final LoanRepository loans;private final DisbursementRepository disbursements;private final RepaymentScheduleRepository schedules;private final Clock clock;
    @Inject public PrepareDisbursementService(LoanRepository loans,DisbursementRepository disbursements,RepaymentScheduleRepository schedules,Clock clock){this.loans=loans;this.disbursements=disbursements;this.schedules=schedules;this.clock=clock;}
    @Transactional public Disbursement prepare(UUID loanId){
        var loan=loans.findByIdForUpdate(loanId).orElseThrow(()->new com.loanorigination.loan.application.exception.LoanNotFoundException(loanId));
        var existing=disbursements.findByLoanIdForUpdate(loanId);
        if(existing.isPresent()&&existing.get().status()==DisbursementStatus.COMPLETED)return existing.get();
        if(loan.status()!=LoanStatus.PENDING_DISBURSEMENT||schedules.findActiveByLoanId(loanId).isPresent())throw new LoanNotEligibleForDisbursementException(loanId);
        Disbursement disbursement=existing.orElseGet(()->Disbursement.create(loanId,loan.principal(),clock.instant()));
        if(disbursement.status()==DisbursementStatus.CANCELLED)throw new LoanNotEligibleForDisbursementException(loanId);
        if(disbursement.status()!=DisbursementStatus.PROCESSING)disbursement.beginProcessing(clock.instant());
        disbursements.save(disbursement);
        // Punto futuro de publicación de DISBURSEMENT_REQUESTED mediante outbox.
        return disbursement;
    }
}
