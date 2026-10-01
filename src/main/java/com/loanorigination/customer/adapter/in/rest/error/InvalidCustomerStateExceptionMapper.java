package com.loanorigination.customer.adapter.in.rest.error;

import com.loanorigination.customer.domain.InvalidCustomerStateException;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidCustomerStateExceptionMapper
        implements ExceptionMapper<InvalidCustomerStateException> {

    @Override
    public Response toResponse(
            InvalidCustomerStateException exception
    ) {

        return Response
                .status(Response.Status.CONFLICT)
                .entity(ApiErrorResponse.of(
                        "INVALID_CUSTOMER_STATE",
                        exception.getMessage()
                ))
                .build();
    }
}