package com.loanorigination.shared.adapter.in.rest;

import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UnexpectedExceptionMapper.class);

    @Override
    public Response toResponse(Throwable failure) {

        // Las excepciones HTTP de JAX-RS ya contienen el status correcto
        // (404, 401, 403, 405, etc.). No debemos convertirlas en 500.
        if (failure instanceof WebApplicationException webException) {
            return webException.getResponse();
        }

        // Solo los errores realmente inesperados llegan hasta acá.
        LOG.error("Unhandled request failure", failure);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiErrorResponse.of(
                        "INTERNAL_ERROR",
                        "An unexpected error occurred"
                ))
                .build();
    }
}