package com.loanorigination.loanoffer.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.loanoffer.application.exception.LoanOfferAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class LoanOfferAlreadyExistsExceptionMapper implements ExceptionMapper<LoanOfferAlreadyExistsException>{public Response toResponse(LoanOfferAlreadyExistsException e){return Response.status(409).entity(ApiErrorResponse.of("LOAN_OFFER_ALREADY_EXISTS",e.getMessage())).build();}}
