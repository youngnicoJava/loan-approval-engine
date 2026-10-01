package com.loanorigination.customer.adapter.in.rest;

import com.loanorigination.customer.adapter.in.rest.dto.CreateCustomerRequest;
import com.loanorigination.customer.adapter.in.rest.dto.CustomerResponse;
import com.loanorigination.customer.application.port.in.ActivateCustomerUseCase;
import com.loanorigination.customer.application.port.in.BlockCustomerUseCase;
import com.loanorigination.customer.application.port.in.CreateCustomerUseCase;
import com.loanorigination.customer.application.port.in.GetCurrentCustomerUseCase;
import com.loanorigination.customer.application.port.in.GetCustomerUseCase;
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

@Path("/api/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customers")
public class CustomerResource {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final GetCurrentCustomerUseCase getCurrentCustomerUseCase;
    private final BlockCustomerUseCase blockCustomerUseCase;
    private final ActivateCustomerUseCase activateCustomerUseCase;

    @Inject
    public CustomerResource(
            CreateCustomerUseCase createCustomerUseCase,
            GetCustomerUseCase getCustomerUseCase,
            GetCurrentCustomerUseCase getCurrentCustomerUseCase,
            BlockCustomerUseCase blockCustomerUseCase,
            ActivateCustomerUseCase activateCustomerUseCase
    ) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.getCurrentCustomerUseCase = getCurrentCustomerUseCase;
        this.blockCustomerUseCase = blockCustomerUseCase;
        this.activateCustomerUseCase = activateCustomerUseCase;
    }

    @POST
    @RolesAllowed("CUSTOMER")
    public Response create(
            @Valid CreateCustomerRequest request
    ) {

        CustomerResponse response = CustomerResponse.from(
                createCustomerUseCase.create(
                        request.toCommand()
                )
        );

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("CUSTOMER")
    public CustomerResponse getCurrentCustomer() {

        return CustomerResponse.from(
                getCurrentCustomerUseCase.getCurrentCustomer()
        );
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({
            "LOAN_OFFICER",
            "AUDITOR",
            "ADMIN"
    })
    public CustomerResponse get(
            @PathParam("id") UUID id
    ) {

        return CustomerResponse.from(
                getCustomerUseCase.get(id)
        );
    }

    @POST
    @Path("/{id}/block")
    @RolesAllowed({
            "LOAN_OFFICER",
            "ADMIN"
    })
    public CustomerResponse block(
            @PathParam("id") UUID id
    ) {

        return CustomerResponse.from(
                blockCustomerUseCase.block(id)
        );
    }

    @POST
    @Path("/{id}/activate")
    @RolesAllowed({
            "LOAN_OFFICER",
            "ADMIN"
    })
    public CustomerResponse activate(
            @PathParam("id") UUID id
    ) {

        return CustomerResponse.from(
                activateCustomerUseCase.activate(id)
        );
    }
}