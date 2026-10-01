package com.loanorigination.customer.adapter.in.rest.error;

import com.loanorigination.customer.application.exception.CustomerAlreadyExistsException;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerAlreadyExistsExceptionMapper
        implements ExceptionMapper<CustomerAlreadyExistsException> {

    @Override
    public Response toResponse(
            CustomerAlreadyExistsException exception
    ) {

        return Response
                .status(Response.Status.CONFLICT)
                .entity(ApiErrorResponse.of(
                        "CUSTOMER_ALREADY_EXISTS",
                        exception.getMessage()
                ))
                .build();
    }
}