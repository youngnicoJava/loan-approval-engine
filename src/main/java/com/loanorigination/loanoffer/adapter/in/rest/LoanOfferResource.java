package com.loanorigination.loanoffer.adapter.in.rest;
import com.loanorigination.loan.adapter.in.rest.dto.LoanResponse;
import com.loanorigination.loanoffer.adapter.in.rest.dto.*;
import com.loanorigination.loanoffer.application.port.in.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import jakarta.transaction.Transactional;
import com.loanorigination.idempotency.application.service.IdempotencyService;
import com.loanorigination.idempotency.adapter.in.rest.RequestFingerprint;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
@Path("/api/v1/loan-offers") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
public class LoanOfferResource {
    private final IssueLoanOfferUseCase issue; private final GetLoanOfferUseCase get; private final GetCustomerLoanOffersUseCase list;
    private final AcceptLoanOfferUseCase accept; private final DeclineLoanOfferUseCase decline; private final IdempotencyService idempotency; private final RequestFingerprint fingerprint;
    @Inject public LoanOfferResource(IssueLoanOfferUseCase issue,GetLoanOfferUseCase get,GetCustomerLoanOffersUseCase list,AcceptLoanOfferUseCase accept,DeclineLoanOfferUseCase decline,IdempotencyService idempotency,RequestFingerprint fingerprint){this.issue=issue;this.get=get;this.list=list;this.accept=accept;this.decline=decline;this.idempotency=idempotency;this.fingerprint=fingerprint;}
    @POST @Path("/from-application/{applicationId}") @RolesAllowed({"LOAN_OFFICER","ADMIN"})
    @Operation(summary = "Emitir oferta", description = "Emite una oferta a partir de una solicitud aprobada.")
    @APIResponses({@APIResponse(responseCode = "201", description = "Oferta creada"), @APIResponse(responseCode = "400", description = "Solicitud inválida"), @APIResponse(responseCode = "401", description = "No autenticado"), @APIResponse(responseCode = "403", description = "Rol insuficiente"), @APIResponse(responseCode = "409", description = "La solicitud no está aprobada o ya tiene oferta")})
    public Response issue(@PathParam("applicationId") UUID appId,@Valid IssueLoanOfferRequest request){var o=issue.issue(appId,request.offeredPrincipal(),request.offeredTermMonths(),request.annualInterestRatePercentage(),request.expiresAt());return Response.status(Response.Status.CREATED).entity(LoanOfferResponse.from(o)).build();}
    @GET @Path("/mine") @RolesAllowed("CUSTOMER") public List<LoanOfferResponse> mine(){return list.getForCurrentCustomer().stream().map(LoanOfferResponse::from).toList();}
    @GET @Path("/{id}") @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"}) public LoanOfferResponse get(@PathParam("id") UUID id){return LoanOfferResponse.from(get.get(id));}
    @POST @Path("/{id}/accept") @RolesAllowed("CUSTOMER") @Transactional @Operation(summary = "Aceptar oferta", description = "Acepta una oferta propia vigente y crea el Loan; requiere Idempotency-Key.") @APIResponses({@APIResponse(responseCode = "200", description = "Oferta aceptada y préstamo creado"), @APIResponse(responseCode = "400", description = "Falta una clave idempotente válida"), @APIResponse(responseCode = "401", description = "No autenticado"), @APIResponse(responseCode = "403", description = "La oferta no pertenece al cliente"), @APIResponse(responseCode = "409", description = "Estado inválido o clave idempotente en conflicto"), @APIResponse(responseCode = "410", description = "Oferta expirada")}) public LoanResponse accept(@HeaderParam("Idempotency-Key") String key,@PathParam("id") UUID id){return idempotency.execute("loan-offers:accept:"+id,key,fingerprint.of(id.toString()),200,LoanResponse.class,()->LoanResponse.from(accept.accept(id))).body();}
    @POST @Path("/{id}/decline") @RolesAllowed("CUSTOMER") public LoanOfferResponse decline(@PathParam("id") UUID id){return LoanOfferResponse.from(decline.decline(id));}
}
