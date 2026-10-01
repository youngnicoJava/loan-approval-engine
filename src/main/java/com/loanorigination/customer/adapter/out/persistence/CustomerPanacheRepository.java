package com.loanorigination.customer.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio técnico de Panache.
 *
 * Está separado del puerto CustomerRepository para evitar acoplar
 * application/domain a Hibernate.
 */
@ApplicationScoped
public class CustomerPanacheRepository
        implements PanacheRepositoryBase<CustomerEntity, UUID> {

    public Optional<CustomerEntity> findByExternalIdentityId(
            String externalIdentityId
    ) {
        return find(
                "externalIdentityId",
                externalIdentityId
        ).firstResultOptional();
    }
}