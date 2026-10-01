package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.application.exception.LoanOfferNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanOfferNotFoundExceptionMapper implements ExceptionMapper<LoanOfferNotFoundException>{public Response toResponse(LoanOfferNotFoundException e){return Response.status(404).entity(ApiErrorResponse.of("LOAN_OFFER_NOT_FOUND",e.getMessage())).build();}}
