package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.domain.LoanOfferExpiredException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanOfferExpiredExceptionMapper implements ExceptionMapper<LoanOfferExpiredException>{public Response toResponse(LoanOfferExpiredException e){return Response.status(410).entity(ApiErrorResponse.of("LOAN_OFFER_EXPIRED",e.getMessage())).build();}}
