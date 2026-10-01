package com.loanorigination.disbursement.adapter.out.local;
import com.loanorigination.disbursement.application.port.out.DisbursementPort;
import com.loanorigination.disbursement.application.port.out.DisbursementResult;
import com.loanorigination.disbursement.domain.Disbursement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Clock;
@ApplicationScoped public class LocalDisbursementAdapter implements DisbursementPort {
    private final Clock clock; @Inject public LocalDisbursementAdapter(Clock clock){this.clock=clock;}
    /** El ID estable hace que una repetición local simule una operación idempotente. */
    public DisbursementResult execute(Disbursement disbursement){return DisbursementResult.completed("LOCAL-"+disbursement.id(),clock.instant());}
}
