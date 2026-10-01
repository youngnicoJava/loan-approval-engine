package com.loanorigination.disbursement.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.disbursement.application.exception.DisbursementNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class DisbursementNotFoundMapper implements ExceptionMapper<DisbursementNotFoundException>{public Response toResponse(DisbursementNotFoundException e){return Response.status(404).entity(ApiErrorResponse.of("DISBURSEMENT_NOT_FOUND",e.getMessage())).build();}}
