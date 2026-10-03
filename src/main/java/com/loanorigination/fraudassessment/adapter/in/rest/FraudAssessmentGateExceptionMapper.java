package com.loanorigination.fraudassessment.adapter.in.rest;

import com.loanorigination.fraudassessment.application.FraudAssessmentGateNotClearedException;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FraudAssessmentGateExceptionMapper implements ExceptionMapper<FraudAssessmentGateNotClearedException> {
  @Override public Response toResponse(FraudAssessmentGateNotClearedException e) {
    return Response.status(Response.Status.CONFLICT)
        .entity(ApiErrorResponse.of("FRAUD_ASSESSMENT_NOT_CLEARED",
            "A fraud PASS decision is required before approving an application or issuing an offer"))
        .build();
  }
}
