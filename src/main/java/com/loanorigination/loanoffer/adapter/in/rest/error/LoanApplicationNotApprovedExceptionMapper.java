package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.application.exception.LoanApplicationNotApprovedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanApplicationNotApprovedExceptionMapper implements ExceptionMapper<LoanApplicationNotApprovedException>{public Response toResponse(LoanApplicationNotApprovedException e){return Response.status(409).entity(ApiErrorResponse.of("LOAN_APPLICATION_NOT_APPROVED",e.getMessage())).build();}}
