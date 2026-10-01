package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.application.exception.InvalidLoanOfferTermsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class InvalidLoanOfferTermsExceptionMapper implements ExceptionMapper<InvalidLoanOfferTermsException>{public Response toResponse(InvalidLoanOfferTermsException e){return Response.status(422).entity(ApiErrorResponse.of("INVALID_LOAN_OFFER_TERMS",e.getMessage())).build();}}
