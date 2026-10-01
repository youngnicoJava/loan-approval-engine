package com.loanorigination.customer.adapter.out.persistence;

import com.loanorigination.customer.application.port.out.CustomerRepository;
import com.loanorigination.customer.domain.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomerPersistenceAdapter
        implements CustomerRepository {

    private final CustomerPanacheRepository repository;

    @Inject
    public CustomerPersistenceAdapter(
            CustomerPanacheRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void save(Customer customer) {

        CustomerEntity entity = repository
                .findByIdOptional(customer.id())
                .orElse(null);

        if (entity == null) {
            repository.persist(
                    CustomerEntity.fromDomain(customer)
            );
            return;
        }

        entity.updateFromDomain(customer);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {

        return repository
                .findByIdOptional(customerId)
                .map(CustomerEntity::toDomain);
    }

    @Override
    public boolean existsByExternalIdentityId(
            String externalIdentityId
    ) {

        return repository
                .findByExternalIdentityId(externalIdentityId)
                .isPresent();
    }

    @Override
    public Optional<Customer> findByExternalIdentityId(
            String externalIdentityId
    ) {
        return repository
                .findByExternalIdentityId(externalIdentityId)
                .map(CustomerEntity::toDomain);
    }
}