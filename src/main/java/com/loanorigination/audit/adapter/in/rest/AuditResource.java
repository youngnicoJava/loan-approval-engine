package com.loanorigination.audit.adapter.in.rest;
import com.loanorigination.audit.adapter.in.rest.dto.AuditEventResponse;
import com.loanorigination.audit.application.port.out.AuditRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
@Path("/api/v1/audit/events") @Produces(MediaType.APPLICATION_JSON)
public class AuditResource {
 private final AuditRepository audit;
 @Inject public AuditResource(AuditRepository audit){this.audit=audit;}
 @GET @RolesAllowed({"AUDITOR","ADMIN"}) public List<AuditEventResponse> list(@QueryParam("offset") @DefaultValue("0") int offset,@QueryParam("limit") @DefaultValue("50") int limit){validate(offset,limit);return audit.findAll(offset,limit).stream().map(AuditEventResponse::from).toList();}
 @GET @Path("/{aggregateId}") @RolesAllowed({"AUDITOR","ADMIN"}) public List<AuditEventResponse> byAggregate(@PathParam("aggregateId") UUID id,@QueryParam("offset") @DefaultValue("0") int offset,@QueryParam("limit") @DefaultValue("50") int limit){validate(offset,limit);return audit.findByAggregateId(id,offset,limit).stream().map(AuditEventResponse::from).toList();}
 private void validate(int offset,int limit){if(offset<0||offset>1_000_000||limit<1||limit>100)throw new BadRequestException("offset must be between 0 and 1000000 and limit must be between 1 and 100");}
}
