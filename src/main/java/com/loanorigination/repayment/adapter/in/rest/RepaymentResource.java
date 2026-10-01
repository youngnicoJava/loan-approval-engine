package com.loanorigination.repayment.adapter.in.rest;
import com.loanorigination.repayment.adapter.in.rest.dto.*;
import com.loanorigination.repayment.application.port.in.GetInstallmentsUseCase;
import com.loanorigination.repayment.application.port.in.GetRepaymentScheduleUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
@Path("/api/v1/loans/{loanId}") @Produces(MediaType.APPLICATION_JSON)
public class RepaymentResource{
    private final GetRepaymentScheduleUseCase schedules;private final GetInstallmentsUseCase installments;
    @Inject public RepaymentResource(GetRepaymentScheduleUseCase schedules,GetInstallmentsUseCase installments){this.schedules=schedules;this.installments=installments;}
    @GET @Path("/repayment-schedule") @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"})
    public RepaymentScheduleResponse getSchedule(@PathParam("loanId") UUID loanId){return RepaymentScheduleResponse.from(schedules.getForLoan(loanId));}
    @GET @Path("/installments") @RolesAllowed({"CUSTOMER","LOAN_OFFICER","AUDITOR","ADMIN"})
    public List<InstallmentResponse> getInstallments(@PathParam("loanId") UUID loanId){return installments.getForLoan(loanId).stream().map(InstallmentResponse::from).toList();}
}
