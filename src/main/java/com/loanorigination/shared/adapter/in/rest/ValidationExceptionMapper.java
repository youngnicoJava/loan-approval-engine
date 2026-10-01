package com.loanorigination.shared.adapter.in.rest;

import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException failure) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiErrorResponse.of("VALIDATION_ERROR", "One or more request fields are invalid"))
                .build();
    }
}
