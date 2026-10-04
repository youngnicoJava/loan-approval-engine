package com.loanorigination.loanapplication.adapter.in.rest;

import com.loanorigination.loanapplication.adapter.in.rest.dto.CreateLoanApplicationRequest;
import com.loanorigination.loanapplication.adapter.in.rest.dto.LoanApplicationResponse;
import com.loanorigination.loanapplication.application.port.in.CreateLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.GetLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.SubmitLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.ListLoanApplicationsUseCase;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;
import com.loanorigination.riskassessment.adapter.in.rest.RiskAssessmentResponse;
import com.loanorigination.riskassessment.application.port.in.ApproveLoanApplicationUseCase;
import com.loanorigination.riskassessment.application.port.in.EvaluateLoanApplicationUseCase;
import com.loanorigination.riskassessment.application.port.in.RejectLoanApplicationUseCase;
import com.loanorigination.riskassessment.application.port.in.GetLatestRiskAssessmentUseCase;
import com.loanorigination.riskassessment.application.port.in.RequestExternalRiskAssessmentUseCase;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
import jakarta.ws.rs.HeaderParam;
import jakarta.transaction.Transactional;
import com.loanorigination.idempotency.application.service.IdempotencyService;
import com.loanorigination.idempotency.adapter.in.rest.RequestFingerprint;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.UUID;
import java.util.List;

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
    private final IdempotencyService idempotency;
    private final RequestFingerprint fingerprint;
    private final ListLoanApplicationsUseCase listLoanApplicationsUseCase;
    private final GetLatestRiskAssessmentUseCase getLatestRiskAssessmentUseCase;
    private final RequestExternalRiskAssessmentUseCase requestExternalRiskAssessmentUseCase;
    @ConfigProperty(name = "app.risk.mode", defaultValue = "LOCAL")
    String riskMode;

    @Inject
    public LoanApplicationResource(
            CreateLoanApplicationUseCase createLoanApplicationUseCase,
            GetLoanApplicationUseCase getLoanApplicationUseCase,
            SubmitLoanApplicationUseCase submitLoanApplicationUseCase,
            EvaluateLoanApplicationUseCase evaluateLoanApplicationUseCase,
            ApproveLoanApplicationUseCase approveLoanApplicationUseCase,
            RejectLoanApplicationUseCase rejectLoanApplicationUseCase,
            IdempotencyService idempotency,
            RequestFingerprint fingerprint,
            ListLoanApplicationsUseCase listLoanApplicationsUseCase,
            GetLatestRiskAssessmentUseCase getLatestRiskAssessmentUseCase,
            RequestExternalRiskAssessmentUseCase requestExternalRiskAssessmentUseCase
    ) {
        this.createLoanApplicationUseCase = createLoanApplicationUseCase;
        this.getLoanApplicationUseCase = getLoanApplicationUseCase;
        this.submitLoanApplicationUseCase = submitLoanApplicationUseCase;
        this.evaluateLoanApplicationUseCase = evaluateLoanApplicationUseCase;
        this.approveLoanApplicationUseCase = approveLoanApplicationUseCase;
        this.rejectLoanApplicationUseCase = rejectLoanApplicationUseCase;
        this.idempotency=idempotency;
        this.fingerprint=fingerprint;
        this.listLoanApplicationsUseCase = listLoanApplicationsUseCase;
        this.getLatestRiskAssessmentUseCase = getLatestRiskAssessmentUseCase;
        this.requestExternalRiskAssessmentUseCase = requestExternalRiskAssessmentUseCase;
    }

    @GET
    @Path("/mine")
    @RolesAllowed("CUSTOMER")
    public List<LoanApplicationResponse> mine() {
        return listLoanApplicationsUseCase.forCurrentCustomer().stream().map(LoanApplicationResponse::from).toList();
    }

    @GET
    @RolesAllowed({"LOAN_OFFICER", "ADMIN"})
    @Operation(summary = "Cola operativa de solicitudes", description = "Filtra por estado real del dominio; admite paginación y por defecto muestra todos los estados.")
    public List<LoanApplicationResponse> queue(
            @jakarta.ws.rs.QueryParam("status") LoanApplicationStatus status,
            @jakarta.ws.rs.QueryParam("offset") @jakarta.ws.rs.DefaultValue("0") int offset,
            @jakarta.ws.rs.QueryParam("limit") @jakarta.ws.rs.DefaultValue("50") int limit
    ) {
        if (offset < 0 || offset > 1_000_000 || limit < 1 || limit > 100) {
            throw new jakarta.ws.rs.BadRequestException("offset must be between 0 and 1000000 and limit between 1 and 100");
        }
        return listLoanApplicationsUseCase.forOperations(status, offset, limit).stream().map(LoanApplicationResponse::from).toList();
    }

    @POST
    @RolesAllowed("CUSTOMER")
    @Transactional
    @Operation(summary = "Crear solicitud de préstamo", description = "Crea una solicitud para el cliente autenticado; requiere Idempotency-Key.")
    public Response create(
            @HeaderParam("Idempotency-Key") String key,
            @Valid CreateLoanApplicationRequest request
    ) {
        var result=idempotency.execute("loan-applications:create",key,fingerprint.of(request),201,LoanApplicationResponse.class,()->
                LoanApplicationResponse.from(
                        createLoanApplicationUseCase.create(
                                request.toCommand()
                        )
                ));
        return Response.status(result.status()).entity(result.body()).build();
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

    @GET
    @Path("/{id}/risk-assessment")
    @RolesAllowed({"LOAN_OFFICER", "AUDITOR", "ADMIN"})
    public Response riskAssessment(@PathParam("id") UUID id) {
        return getLatestRiskAssessmentUseCase.getLatest(id)
                .map(RiskAssessmentResponse::from)
                .map(value -> Response.ok(value).build())
                .orElseGet(() -> Response.noContent().build());
    }

    @POST
    @Path("/{id}/submit")
    @RolesAllowed("CUSTOMER")
    @Transactional
    @Operation(summary = "Enviar solicitud", description = "Envía una solicitud propia para su evaluación.")
    public LoanApplicationResponse submit(
            @HeaderParam("Idempotency-Key") String key,
            @PathParam("id") UUID id
    ) {
        return idempotency.execute("loan-applications:submit:"+id,key,fingerprint.of(id.toString()),200,LoanApplicationResponse.class,
                ()->LoanApplicationResponse.from(submitLoanApplicationUseCase.submit(id))).body();
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
    @Operation(summary = "Evaluar riesgo", description = "Ejecuta la evaluación de riesgo para una solicitud existente.")
    public Response evaluate(
            @PathParam("id") UUID id
    ) {
        if ("KAFKA".equalsIgnoreCase(riskMode)) {
            requestExternalRiskAssessmentUseCase.request(id);
            return Response.accepted(new ExternalRiskRequestResponse(id, "UNDER_REVIEW", "Risk assessment queued for Credit Risk Engine.")).build();
        }
        return Response.ok(RiskAssessmentResponse.from(evaluateLoanApplicationUseCase.evaluate(id))).build();
    }

    public record ExternalRiskRequestResponse(UUID loanApplicationId, String status, String message) { }

    /**
     * Resolución manual de una solicitud que quedó REFER.
     */
    @POST
    @Path("/{id}/approve")
    @RolesAllowed({
            "LOAN_OFFICER",
            "ADMIN"
    })
    @Operation(summary = "Aprobar solicitud", description = "Resolución manual disponible para personal autorizado; requiere Idempotency-Key.")
    @Transactional
    public LoanApplicationResponse approve(
            @HeaderParam("Idempotency-Key") String key,
            @PathParam("id") UUID id
    ) {
        return idempotency.execute("loan-applications:approve:"+id,key,fingerprint.of(id.toString()),200,LoanApplicationResponse.class,
                ()->LoanApplicationResponse.from(approveLoanApplicationUseCase.approve(id))).body();
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
    @Operation(summary = "Rechazar solicitud", description = "Resolución manual disponible para personal autorizado; requiere Idempotency-Key.")
    @Transactional
    public LoanApplicationResponse reject(
            @HeaderParam("Idempotency-Key") String key,
            @PathParam("id") UUID id
    ) {
        return idempotency.execute("loan-applications:reject:"+id,key,fingerprint.of(id.toString()),200,LoanApplicationResponse.class,
                ()->LoanApplicationResponse.from(rejectLoanApplicationUseCase.reject(id))).body();
    }
}
