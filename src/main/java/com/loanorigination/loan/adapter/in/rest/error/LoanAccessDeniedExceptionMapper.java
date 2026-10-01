package com.loanorigination.loan.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loan.application.exception.LoanAccessDeniedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanAccessDeniedExceptionMapper implements ExceptionMapper<LoanAccessDeniedException>{public Response toResponse(LoanAccessDeniedException e){return Response.status(403).entity(ApiErrorResponse.of("LOAN_ACCESS_DENIED",e.getMessage())).build();}}
