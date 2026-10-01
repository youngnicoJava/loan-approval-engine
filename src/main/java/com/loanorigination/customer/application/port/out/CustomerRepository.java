package com.loanorigination.customer.application.port.out;

import com.loanorigination.customer.domain.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia del agregado Customer.
 *
 * La aplicación depende de este contrato, no de Panache ni de JPA.
 */
public interface CustomerRepository {

    void save(Customer customer);

    Optional<Customer> findById(UUID customerId);

    Optional<Customer> findByExternalIdentityId(
            String externalIdentityId
    );

    boolean existsByExternalIdentityId(
            String externalIdentityId
    );
}