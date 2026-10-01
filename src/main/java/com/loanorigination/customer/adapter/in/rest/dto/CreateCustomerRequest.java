package com.loanorigination.customer.adapter.in.rest.dto;

import com.loanorigination.customer.application.command.CreateCustomerCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @NotBlank
        @Size(max = 150)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 320)
        String email

) {

    public CreateCustomerCommand toCommand() {

        return new CreateCustomerCommand(
                fullName,
                email
        );
    }
}