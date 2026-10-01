package com.loanorigination.disbursement.adapter.in.rest;
import com.loanorigination.disbursement.adapter.in.rest.dto.DisbursementResponse;
import com.loanorigination.disbursement.application.port.in.DisburseLoanUseCase;
import com.loanorigination.disbursement.application.port.in.GetLoanDisbursementUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import com.loanorigination.idempotency.application.service.IdempotencyService;
import com.loanorigination.idempotency.adapter.in.rest.RequestFingerprint;
@Path("/api/v1/loans/{loanId}/disbursement") @Produces(MediaType.APPLICATION_JSON)
public class DisbursementResource{
    private final DisburseLoanUseCase disburse;private final GetLoanDisbursementUseCase get;private final IdempotencyService idempotency;private final RequestFingerprint fingerprint;
    @Inject public DisbursementResource(DisburseLoanUseCase disburse,GetLoanDisbursementUseCase get,IdempotencyService idempotency,RequestFingerprint fingerprint){this.disburse=disburse;this.get=get;this.idempotency=idempotency;this.fingerprint=fingerprint;}
    @POST @RolesAllowed({"LOAN_OFFICER","ADMIN"}) public DisbursementResponse disburse(@HeaderParam("Idempotency-Key") String key,@PathParam("loanId") UUID loanId){
        var claim=idempotency.beginExternal("loans:disbursement:"+loanId,key,fingerprint.of(loanId.toString()),DisbursementResponse.class);
        if(claim.completed())return claim.cachedBody();
        try{var response=DisbursementResponse.from(disburse.disburse(loanId));idempotency.completeExternal(claim,200,response);return response;}
        catch(RuntimeException e){idempotency.releaseExternal(claim);throw e;}
    }
    @GET @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"}) public DisbursementResponse get(@PathParam("loanId") UUID loanId){return DisbursementResponse.from(get.getByLoanId(loanId));}
}
