package com.loanorigination.loanapplication.adapter.in.rest.error;

import com.loanorigination.loanapplication.application.exception.LoanApplicationNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoanApplicationNotFoundExceptionMapper
        implements ExceptionMapper<LoanApplicationNotFoundException> {

    @Override
    public Response toResponse(LoanApplicationNotFoundException exception) {

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(ApiErrorResponse.of(
                        "LOAN_APPLICATION_NOT_FOUND",
                        exception.getMessage()
                ))
                .build();
    }
}