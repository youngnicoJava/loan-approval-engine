package com.loanorigination.disbursement.adapter.in.rest;
import com.loanorigination.disbursement.adapter.in.rest.dto.DisbursementResponse;
import com.loanorigination.disbursement.application.port.in.DisburseLoanUseCase;
import com.loanorigination.disbursement.application.port.in.GetLoanDisbursementUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
@Path("/api/v1/loans/{loanId}/disbursement") @Produces(MediaType.APPLICATION_JSON)
public class DisbursementResource{
    private final DisburseLoanUseCase disburse;private final GetLoanDisbursementUseCase get;
    @Inject public DisbursementResource(DisburseLoanUseCase disburse,GetLoanDisbursementUseCase get){this.disburse=disburse;this.get=get;}
    @POST @RolesAllowed({"LOAN_OFFICER","ADMIN"}) public DisbursementResponse disburse(@PathParam("loanId") UUID loanId){return DisbursementResponse.from(disburse.disburse(loanId));}
    @GET @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"}) public DisbursementResponse get(@PathParam("loanId") UUID loanId){return DisbursementResponse.from(get.getByLoanId(loanId));}
}
