package com.loanorigination.loanapplication.adapter.in.rest.error;

import com.loanorigination.loanapplication.domain.InvalidLoanApplicationStateException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidLoanApplicationStateExceptionMapper
        implements ExceptionMapper<InvalidLoanApplicationStateException> {

    @Override
    public Response toResponse(
            InvalidLoanApplicationStateException exception
    ) {

        return Response
                .status(Response.Status.CONFLICT)
                .entity(ApiErrorResponse.of(
                        "INVALID_LOAN_APPLICATION_STATE",
                        exception.getMessage()
                ))
                .build();
    }
}