package com.loanorigination.customer.adapter.in.rest.error;

import com.loanorigination.customer.application.exception.CustomerBlockedException;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerBlockedExceptionMapper
        implements ExceptionMapper<CustomerBlockedException> {

    @Override
    public Response toResponse(
            CustomerBlockedException exception
    ) {

        return Response
                .status(Response.Status.CONFLICT)
                .entity(ApiErrorResponse.of(
                        "CUSTOMER_BLOCKED",
                        exception.getMessage()
                ))
                .build();
    }
}