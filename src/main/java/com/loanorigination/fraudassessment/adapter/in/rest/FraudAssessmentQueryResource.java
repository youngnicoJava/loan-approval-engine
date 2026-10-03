package com.loanorigination.fraudassessment.adapter.in.rest;

import com.loanorigination.fraudassessment.application.FraudAssessmentRepository;
import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/api/v1/loan-applications/{applicationId}/fraud-assessment")
@Produces(MediaType.APPLICATION_JSON)
public class FraudAssessmentQueryResource {
  private final FraudAssessmentRepository assessments;
  @Inject public FraudAssessmentQueryResource(FraudAssessmentRepository a) { assessments=a; }
  @GET @RolesAllowed({"LOAN_OFFICER","AUDITOR","ADMIN"})
  public FraudAssessmentResult latest(@PathParam("applicationId") UUID applicationId) {
    return assessments.findLatestByApplicationId(applicationId).orElseThrow(NotFoundException::new);
  }
}
