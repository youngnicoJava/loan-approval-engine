package com.loanorigination.customer.adapter.in.rest.error;

import com.loanorigination.customer.application.exception.CustomerNotFoundException;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerNotFoundExceptionMapper
        implements ExceptionMapper<CustomerNotFoundException> {

    @Override
    public Response toResponse(
            CustomerNotFoundException exception
    ) {

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(ApiErrorResponse.of(
                        "CUSTOMER_NOT_FOUND",
                        exception.getMessage()
                ))
                .build();
    }
}