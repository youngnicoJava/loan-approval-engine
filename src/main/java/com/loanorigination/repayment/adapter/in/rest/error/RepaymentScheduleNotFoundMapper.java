package com.loanorigination.repayment.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.repayment.application.exception.RepaymentScheduleNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class RepaymentScheduleNotFoundMapper implements ExceptionMapper<RepaymentScheduleNotFoundException>{public Response toResponse(RepaymentScheduleNotFoundException e){return Response.status(404).entity(ApiErrorResponse.of("REPAYMENT_SCHEDULE_NOT_FOUND",e.getMessage())).build();}}
