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
@Path("/api/v1/loan-offers") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
public class LoanOfferResource {
    private final IssueLoanOfferUseCase issue; private final GetLoanOfferUseCase get; private final GetCustomerLoanOffersUseCase list;
    private final AcceptLoanOfferUseCase accept; private final DeclineLoanOfferUseCase decline; private final IdempotencyService idempotency; private final RequestFingerprint fingerprint;
    @Inject public LoanOfferResource(IssueLoanOfferUseCase issue,GetLoanOfferUseCase get,GetCustomerLoanOffersUseCase list,AcceptLoanOfferUseCase accept,DeclineLoanOfferUseCase decline,IdempotencyService idempotency,RequestFingerprint fingerprint){this.issue=issue;this.get=get;this.list=list;this.accept=accept;this.decline=decline;this.idempotency=idempotency;this.fingerprint=fingerprint;}
    @POST @Path("/from-application/{applicationId}") @RolesAllowed({"LOAN_OFFICER","ADMIN"})
    public Response issue(@PathParam("applicationId") UUID appId,@Valid IssueLoanOfferRequest request){var o=issue.issue(appId,request.offeredPrincipal(),request.offeredTermMonths(),request.annualInterestRatePercentage(),request.expiresAt());return Response.status(Response.Status.CREATED).entity(LoanOfferResponse.from(o)).build();}
    @GET @Path("/mine") @RolesAllowed("CUSTOMER") public List<LoanOfferResponse> mine(){return list.getForCurrentCustomer().stream().map(LoanOfferResponse::from).toList();}
    @GET @Path("/{id}") @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"}) public LoanOfferResponse get(@PathParam("id") UUID id){return LoanOfferResponse.from(get.get(id));}
    @POST @Path("/{id}/accept") @RolesAllowed("CUSTOMER") @Transactional public LoanResponse accept(@HeaderParam("Idempotency-Key") String key,@PathParam("id") UUID id){return idempotency.execute("loan-offers:accept:"+id,key,fingerprint.of(id.toString()),200,LoanResponse.class,()->LoanResponse.from(accept.accept(id))).body();}
    @POST @Path("/{id}/decline") @RolesAllowed("CUSTOMER") public LoanOfferResponse decline(@PathParam("id") UUID id){return LoanOfferResponse.from(decline.decline(id));}
}
