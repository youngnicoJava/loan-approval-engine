package com.loanorigination.loan.adapter.in.rest;
import com.loanorigination.loan.adapter.in.rest.dto.LoanResponse;
import com.loanorigination.loan.application.port.in.GetCustomerLoansUseCase;
import com.loanorigination.loan.application.port.in.GetLoanUseCase;
import com.loanorigination.loan.application.port.in.ListOperationalLoansUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
@Path("/api/v1/loans") @Produces(MediaType.APPLICATION_JSON)
public class LoanResource {
    private final GetLoanUseCase get; private final GetCustomerLoansUseCase list;
    private final ListOperationalLoansUseCase operations;
    @Inject public LoanResource(GetLoanUseCase get,GetCustomerLoansUseCase list,ListOperationalLoansUseCase operations){this.get=get;this.list=list;this.operations=operations;}
    @GET @Path("/mine") @RolesAllowed("CUSTOMER") public List<LoanResponse> mine(){return list.getForCurrentCustomer().stream().map(LoanResponse::from).toList();}
    @GET @RolesAllowed({"LOAN_OFFICER","ADMIN"}) public List<LoanResponse> operations(@QueryParam("status") com.loanorigination.loan.domain.LoanStatus status,@QueryParam("limit") @DefaultValue("100") int limit){if(limit<1||limit>100)throw new BadRequestException("limit must be between 1 and 100");return operations.list(status,limit).stream().map(LoanResponse::from).toList();}
    @GET @Path("/{id}") @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"}) public LoanResponse get(@PathParam("id") UUID id){return LoanResponse.from(get.get(id));}
}
