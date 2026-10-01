package com.loanorigination.disbursement.application.service;
import com.loanorigination.disbursement.application.exception.DisbursementNotFoundException;
import com.loanorigination.disbursement.application.port.in.GetLoanDisbursementUseCase;
import com.loanorigination.disbursement.application.port.out.DisbursementRepository;
import com.loanorigination.disbursement.domain.Disbursement;
import com.loanorigination.loan.application.port.in.GetLoanUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
@ApplicationScoped public class GetLoanDisbursementService implements GetLoanDisbursementUseCase{
    private final GetLoanUseCase loans;private final DisbursementRepository disbursements;
    @Inject public GetLoanDisbursementService(GetLoanUseCase loans,DisbursementRepository disbursements){this.loans=loans;this.disbursements=disbursements;}
    public Disbursement getByLoanId(UUID loanId){loans.get(loanId);return disbursements.findByLoanId(loanId).orElseThrow(()->new DisbursementNotFoundException(loanId));}
}
