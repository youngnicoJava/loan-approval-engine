package com.loanorigination.customer.application.service;

import com.loanorigination.customer.application.command.CreateCustomerCommand;
import com.loanorigination.customer.application.exception.CustomerAlreadyExistsException;
import com.loanorigination.customer.application.port.in.CreateCustomerUseCase;
import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.security.domain.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Caso de uso de alta del perfil funcional Customer.
 *
 * La identidad externa no proviene del body HTTP: se obtiene del
 * usuario autenticado. Esto evita que un cliente pueda registrar
 * información bajo la identidad de otra persona.
 */
@ApplicationScoped
public class CreateCustomerService
        implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CurrentUserPort currentUserPort;

    @Inject
    public CreateCustomerService(
            CustomerRepository customerRepository,
            CurrentUserPort currentUserPort
    ) {
        this.customerRepository = customerRepository;
        this.currentUserPort = currentUserPort;
    }

    @Override
    @Transactional
    public Customer create(CreateCustomerCommand command) {

        AuthenticatedUser currentUser =
                currentUserPort.getCurrentUser();

        String externalIdentityId =
                currentUser.externalIdentityId();

        if (customerRepository.existsByExternalIdentityId(
                externalIdentityId
        )) {
            throw new CustomerAlreadyExistsException(
                    externalIdentityId
            );
        }

        Customer customer = Customer.create(
                externalIdentityId,
                command.fullName(),
                command.email(),
                Instant.now()
        );

        customerRepository.save(customer);

        return customer;
    }
}