package com.loanorigination.loanapplication.adapter.in.rest;

import com.loanorigination.loanapplication.adapter.in.rest.dto.CreateLoanApplicationRequest;
import com.loanorigination.loanapplication.adapter.in.rest.dto.LoanApplicationResponse;
import com.loanorigination.loanapplication.application.port.in.CreateLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.GetLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.SubmitLoanApplicationUseCase;
import com.loanorigination.riskassessment.adapter.in.rest.RiskAssessmentResponse;
import com.loanorigination.riskassessment.application.port.in.ApproveLoanApplicationUseCase;
import com.loanorigination.riskassessment.application.port.in.EvaluateLoanApplicationUseCase;
import com.loanorigination.riskassessment.application.port.in.RejectLoanApplicationUseCase;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/v1/loan-applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Loan Applications")
public class LoanApplicationResource {

    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final GetLoanApplicationUseCase getLoanApplicationUseCase;
    private final SubmitLoanApplicationUseCase submitLoanApplicationUseCase;

    private final EvaluateLoanApplicationUseCase evaluateLoanApplicationUseCase;
    private final ApproveLoanApplicationUseCase approveLoanApplicationUseCase;
    private final RejectLoanApplicationUseCase rejectLoanApplicationUseCase;

    @Inject
    public LoanApplicationResource(
            CreateLoanApplicationUseCase createLoanApplicationUseCase,
            GetLoanApplicationUseCase getLoanApplicationUseCase,
            SubmitLoanApplicationUseCase submitLoanApplicationUseCase,
            EvaluateLoanApplicationUseCase evaluateLoanApplicationUseCase,
            ApproveLoanApplicationUseCase approveLoanApplicationUseCase,
            RejectLoanApplicationUseCase rejectLoanApplicationUseCase
    ) {
        this.createLoanApplicationUseCase = createLoanApplicationUseCase;
        this.getLoanApplicationUseCase = getLoanApplicationUseCase;
        this.submitLoanApplicationUseCase = submitLoanApplicationUseCase;
        this.evaluateLoanApplicationUseCase = evaluateLoanApplicationUseCase;
        this.approveLoanApplicationUseCase = approveLoanApplicationUseCase;
        this.rejectLoanApplicationUseCase = rejectLoanApplicationUseCase;
    }

    @POST
    @RolesAllowed("CUSTOMER")
    public Response create(
            @Valid CreateLoanApplicationRequest request
    ) {

        LoanApplicationResponse response =
                LoanApplicationResponse.from(
                        createLoanApplicationUseCase.create(
                                request.toCommand()
                        )
                );

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({
            "CUSTOMER",
            "LOAN_OFFICER",
            "AUDITOR",
            "ADMIN"
    })
    public LoanApplicationResponse get(
            @PathParam("id") UUID id
    ) {

        return LoanApplicationResponse.from(
                getLoanApplicationUseCase.get(id)
        );
    }

    @POST
    @Path("/{id}/submit")
    @RolesAllowed("CUSTOMER")
    public LoanApplicationResponse submit(
            @PathParam("id") UUID id
    ) {

        return LoanApplicationResponse.from(
                submitLoanApplicationUseCase.submit(id)
        );
    }

    /**
     * Inicia la evaluación de riesgo.
     *
     * Actualmente esta operación es disparada por un operador.
     * Posteriormente puede ser ejecutada automáticamente mediante
     * un evento de dominio/outbox.
     */
    @POST
    @Path("/{id}/evaluate")
    @RolesAllowed({
            "LOAN_OFFICER",
            "ADMIN"
    })
    public RiskAssessmentResponse evaluate(
            @PathParam("id") UUID id
    ) {

        return RiskAssessmentResponse.from(
                evaluateLoanApplicationUseCase.evaluate(id)
        );
    }

    /**
     * Resolución manual de una solicitud que quedó REFER.
     */
    @POST
    @Path("/{id}/approve")
    @RolesAllowed({
            "LOAN_OFFICER",
            "ADMIN"
    })
    public LoanApplicationResponse approve(
            @PathParam("id") UUID id
    ) {

        return LoanApplicationResponse.from(
                approveLoanApplicationUseCase.approve(id)
        );
    }

    /**
     * Resolución manual de una solicitud que quedó UNDER_REVIEW.
     */
    @POST
    @Path("/{id}/reject")
    @RolesAllowed({
            "LOAN_OFFICER",
            "ADMIN"
    })
    public LoanApplicationResponse reject(
            @PathParam("id") UUID id
    ) {

        return LoanApplicationResponse.from(
                rejectLoanApplicationUseCase.reject(id)
        );
    }
}