package com.loanorigination.disbursement.application.service;
import com.loanorigination.disbursement.application.exception.LoanNotEligibleForDisbursementException;
import com.loanorigination.disbursement.application.port.out.DisbursementRepository;
import com.loanorigination.disbursement.application.port.out.DisbursementResult;
import com.loanorigination.disbursement.domain.Disbursement;
import com.loanorigination.disbursement.domain.DisbursementStatus;
import com.loanorigination.loan.application.port.out.LoanRepository;
import com.loanorigination.loan.domain.LoanStatus;
import com.loanorigination.repayment.application.port.out.AmortizationCalculatorPort;
import com.loanorigination.repayment.application.port.out.RepaymentScheduleRepository;
import com.loanorigination.repayment.application.exception.RepaymentScheduleAlreadyExistsException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.UUID;
@ApplicationScoped public class FinalizeDisbursementService{
    private final LoanRepository loans;private final DisbursementRepository disbursements;private final RepaymentScheduleRepository schedules;private final AmortizationCalculatorPort calculator;private final Clock clock;
    @Inject public FinalizeDisbursementService(LoanRepository loans,DisbursementRepository disbursements,RepaymentScheduleRepository schedules,AmortizationCalculatorPort calculator,Clock clock){this.loans=loans;this.disbursements=disbursements;this.schedules=schedules;this.calculator=calculator;this.clock=clock;}
    @Transactional public Disbursement finish(UUID loanId,DisbursementResult result){
        var loan=loans.findByIdForUpdate(loanId).orElseThrow(()->new com.loanorigination.loan.application.exception.LoanNotFoundException(loanId));
        Disbursement disbursement=disbursements.findByLoanIdForUpdate(loanId).orElseThrow(()->new LoanNotEligibleForDisbursementException(loanId));
        if(disbursement.status()==DisbursementStatus.COMPLETED)return disbursement;
        if(disbursement.status()!=DisbursementStatus.PROCESSING)throw new LoanNotEligibleForDisbursementException(loanId);
        if(!result.completed()){
            disbursement.fail(clock.instant(),result.failureReason()==null?"Disbursement provider reported failure":result.failureReason());
            disbursements.save(disbursement);return disbursement;
        }
        if(loan.status()!=LoanStatus.PENDING_DISBURSEMENT)throw new LoanNotEligibleForDisbursementException(loanId);
        if(schedules.findActiveByLoanId(loanId).isPresent())throw new RepaymentScheduleAlreadyExistsException(loanId);
        var schedule=calculator.generateSchedule(loan,result.completedAt(),clock.instant());
        disbursement.complete(result.completedAt(),result.externalReference());
        loan.activate(result.completedAt());
        disbursements.save(disbursement);loans.save(loan);schedules.save(schedule);
        // Aquí se podrán emitir LOAN_DISBURSED y REPAYMENT_SCHEDULE_CREATED mediante outbox.
        return disbursement;
    }
}
