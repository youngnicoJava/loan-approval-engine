package com.loanorigination.customer.application.command;

import java.util.Objects;

public record CreateCustomerCommand(
        String fullName,
        String email
) {

    public CreateCustomerCommand {
        Objects.requireNonNull(fullName, "fullName cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
    }
}