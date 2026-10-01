package com.loanorigination.idempotency.adapter.in.rest;

import com.loanorigination.idempotency.application.exception.*;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider public class IdempotencyExceptionMapper implements ExceptionMapper<IdempotencyException> {
 @Override public Response toResponse(IdempotencyException e){
  if(e instanceof IdempotencyKeyRequiredException)return Response.status(400).entity(ApiErrorResponse.of("IDEMPOTENCY_KEY_REQUIRED",e.getMessage())).build();
  if(e instanceof IdempotencyKeyReusedException)return Response.status(409).entity(ApiErrorResponse.of("IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_REQUEST",e.getMessage())).build();
  if(e instanceof IdempotencyInProgressException)return Response.status(409).entity(ApiErrorResponse.of("CONCURRENT_OPERATION",e.getMessage())).build();
  return Response.status(409).entity(ApiErrorResponse.of("IDEMPOTENCY_CONFLICT",e.getMessage())).build();
 }
}
