package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.domain.InvalidLoanOfferStateException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class InvalidLoanOfferStateExceptionMapper implements ExceptionMapper<InvalidLoanOfferStateException>{public Response toResponse(InvalidLoanOfferStateException e){return Response.status(409).entity(ApiErrorResponse.of("INVALID_LOAN_OFFER_STATE",e.getMessage())).build();}}
