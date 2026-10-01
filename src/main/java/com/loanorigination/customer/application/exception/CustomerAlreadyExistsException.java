package com.loanorigination.customer.application.exception;


public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String externalIdentityId) {
        super(
                "Customer already exists for external identity: "
                        + externalIdentityId
        );
     }
}
