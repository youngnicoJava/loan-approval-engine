package com.loanorigination.shared.adapter.in.rest;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@ApplicationScoped
@OpenAPIDefinition(
        info = @Info(title = "Loan Origination Platform API", version = "1.0.0",
                description = "Contrato público v1 para originación y administración de préstamos."))
public class OpenApiConfiguration {
}
