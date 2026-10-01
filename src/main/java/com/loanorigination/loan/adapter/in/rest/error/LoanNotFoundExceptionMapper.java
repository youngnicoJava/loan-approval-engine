package com.loanorigination.loan.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loan.application.exception.LoanNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanNotFoundExceptionMapper implements ExceptionMapper<LoanNotFoundException>{public Response toResponse(LoanNotFoundException e){return Response.status(404).entity(ApiErrorResponse.of("LOAN_NOT_FOUND",e.getMessage())).build();}}
