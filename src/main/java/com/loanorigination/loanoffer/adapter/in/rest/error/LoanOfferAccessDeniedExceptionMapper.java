package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.application.exception.LoanOfferAccessDeniedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanOfferAccessDeniedExceptionMapper implements ExceptionMapper<LoanOfferAccessDeniedException>{public Response toResponse(LoanOfferAccessDeniedException e){return Response.status(403).entity(ApiErrorResponse.of("LOAN_OFFER_ACCESS_DENIED",e.getMessage())).build();}}
