package com.loanorigination.loanapplication.adapter.in.rest;

import com.loanorigination.loanapplication.adapter.in.rest.dto.CreateLoanApplicationRequest;
import com.loanorigination.loanapplication.adapter.in.rest.dto.LoanApplicationResponse;
import com.loanorigination.loanapplication.application.port.in.CreateLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.GetLoanApplicationUseCase;
import com.loanorigination.loanapplication.application.port.in.SubmitLoanApplicationUseCase;
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

    @Inject
    public LoanApplicationResource(
            CreateLoanApplicationUseCase createLoanApplicationUseCase,
            GetLoanApplicationUseCase getLoanApplicationUseCase,
            SubmitLoanApplicationUseCase submitLoanApplicationUseCase
    ) {
        this.createLoanApplicationUseCase = createLoanApplicationUseCase;
        this.getLoanApplicationUseCase = getLoanApplicationUseCase;
        this.submitLoanApplicationUseCase = submitLoanApplicationUseCase;
    }

    @POST
    public Response create(
            @Valid CreateLoanApplicationRequest request
    ) {

        LoanApplicationResponse response = LoanApplicationResponse.from(
                createLoanApplicationUseCase.create(request.toCommand())
        );

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{id}")
    public LoanApplicationResponse get(
            @PathParam("id") UUID id
    ) {

        return LoanApplicationResponse.from(
                getLoanApplicationUseCase.get(id)
        );
    }

    @POST
    @Path("/{id}/submit")
    public LoanApplicationResponse submit(
            @PathParam("id") UUID id
    ) {

        return LoanApplicationResponse.from(
                submitLoanApplicationUseCase.submit(id)
        );
    }
}