package com.loanorigination.disbursement.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.disbursement.application.exception.LoanNotEligibleForDisbursementException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanNotEligibleForDisbursementMapper implements ExceptionMapper<LoanNotEligibleForDisbursementException>{public Response toResponse(LoanNotEligibleForDisbursementException e){return Response.status(409).entity(ApiErrorResponse.of("LOAN_NOT_ELIGIBLE_FOR_DISBURSEMENT",e.getMessage())).build();}}
