package com.loanorigination.loanapplication.adapter.in.rest.error;

import com.loanorigination.loanapplication.application.exception.LoanApplicationAccessDeniedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoanApplicationAccessDeniedExceptionMapper
        implements ExceptionMapper<LoanApplicationAccessDeniedException> {

    @Override
    public Response toResponse(
            LoanApplicationAccessDeniedException exception
    ) {

        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(ApiErrorResponse.of(
                        "LOAN_APPLICATION_ACCESS_DENIED",
                        exception.getMessage()
                ))
                .build();
    }
}