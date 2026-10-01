package com.loanorigination.repayment.adapter.in.rest.error;
import com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse;
import com.loanorigination.repayment.application.exception.RepaymentScheduleAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.*;
@Provider public class RepaymentScheduleAlreadyExistsMapper implements ExceptionMapper<RepaymentScheduleAlreadyExistsException>{public Response toResponse(RepaymentScheduleAlreadyExistsException e){return Response.status(409).entity(ApiErrorResponse.of("REPAYMENT_SCHEDULE_ALREADY_EXISTS",e.getMessage())).build();}}
