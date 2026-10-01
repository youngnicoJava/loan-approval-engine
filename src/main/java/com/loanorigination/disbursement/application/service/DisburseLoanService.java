package com.loanorigination.disbursement.application.service;
import com.loanorigination.disbursement.application.port.in.DisburseLoanUseCase;
import com.loanorigination.disbursement.application.port.out.DisbursementPort;
import com.loanorigination.disbursement.application.port.out.DisbursementResult;
import com.loanorigination.disbursement.domain.Disbursement;
import com.loanorigination.disbursement.domain.DisbursementStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
@ApplicationScoped public class DisburseLoanService implements DisburseLoanUseCase{
    private final PrepareDisbursementService prepare;private final FinalizeDisbursementService finalize;private final DisbursementPort provider;
    @Inject public DisburseLoanService(PrepareDisbursementService prepare,FinalizeDisbursementService finalize,DisbursementPort provider){this.prepare=prepare;this.finalize=finalize;this.provider=provider;}
    public Disbursement disburse(UUID loanId){
        Disbursement disbursement=prepare.prepare(loanId);
        if(disbursement.status()==DisbursementStatus.COMPLETED)return disbursement;
        DisbursementResult result;
        try{result=provider.execute(disbursement);}catch(RuntimeException exception){result=DisbursementResult.failed(exception.getMessage()==null?"Disbursement provider error":exception.getMessage());}
        return finalize.finish(loanId,result);
    }
}
