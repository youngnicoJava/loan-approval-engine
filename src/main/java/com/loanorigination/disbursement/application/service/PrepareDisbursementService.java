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
import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;
@ApplicationScoped public class PrepareDisbursementService{
    private final LoanRepository loans;private final DisbursementRepository disbursements;private final RepaymentScheduleRepository schedules;private final Clock clock;private final WorkflowEventRecorder events;
    @Inject public PrepareDisbursementService(LoanRepository loans,DisbursementRepository disbursements,RepaymentScheduleRepository schedules,Clock clock,WorkflowEventRecorder events){this.loans=loans;this.disbursements=disbursements;this.schedules=schedules;this.clock=clock;this.events=events;}
    @Transactional public Disbursement prepare(UUID loanId){
        var loan=loans.findByIdForUpdate(loanId).orElseThrow(()->new com.loanorigination.loan.application.exception.LoanNotFoundException(loanId));
        var existing=disbursements.findByLoanIdForUpdate(loanId);
        if(existing.isPresent()&&existing.get().status()==DisbursementStatus.COMPLETED)return existing.get();
        if(loan.status()!=LoanStatus.PENDING_DISBURSEMENT||schedules.findActiveByLoanId(loanId).isPresent())throw new LoanNotEligibleForDisbursementException(loanId);
        boolean requested=existing.isEmpty()||existing.get().status()==DisbursementStatus.FAILED;
        Disbursement disbursement=existing.orElseGet(()->Disbursement.create(loanId,loan.principal(),clock.instant()));
        if(disbursement.status()==DisbursementStatus.CANCELLED)throw new LoanNotEligibleForDisbursementException(loanId);
        if(disbursement.status()!=DisbursementStatus.PROCESSING)disbursement.beginProcessing(clock.instant());
        disbursements.save(disbursement);
        if(requested)events.record("DisbursementRequested",AuditAction.DISBURSEMENT_REQUESTED,"Disbursement",disbursement.id(),
                java.util.Map.of("disbursementId",disbursement.id().toString(),"loanId",loanId.toString()));
        return disbursement;
    }
}
