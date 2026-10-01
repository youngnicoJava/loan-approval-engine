package com.loanorigination.customer.adapter.in.rest.error;

import com.loanorigination.customer.application.exception.CustomerAccessDeniedException;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerAccessDeniedExceptionMapper
        implements ExceptionMapper<CustomerAccessDeniedException> {

    @Override
    public Response toResponse(
            CustomerAccessDeniedException exception
    ) {

        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(ApiErrorResponse.of(
                        "CUSTOMER_ACCESS_DENIED",
                        exception.getMessage()
                ))
                .build();
    }
}