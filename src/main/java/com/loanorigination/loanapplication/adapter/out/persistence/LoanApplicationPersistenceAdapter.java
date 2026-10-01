package com.loanorigination.loanapplication.adapter.out.persistence;

import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistencia que implementa el puerto definido por la aplicación.
 *
 * El application layer no conoce Panache ni Hibernate.
 * Este adapter traduce entre el modelo de dominio y la representación
 * persistente gestionada por el repositorio técnico de Panache.
 */
@ApplicationScoped
public class LoanApplicationPersistenceAdapter
        implements LoanApplicationRepository {

    private final LoanApplicationPanacheRepository repository;

    @Inject
    public LoanApplicationPersistenceAdapter(
            LoanApplicationPanacheRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void save(LoanApplication loanApplication) {

        LoanApplicationEntity entity = repository
                .findByIdOptional(loanApplication.id())
                .orElse(null);

        if (entity == null) {
            repository.persist(
                    LoanApplicationEntity.fromDomain(loanApplication)
            );
            return;
        }

        /*
         * El agregado ya fue modificado y validado por el dominio.
         * Acá solamente reflejamos ese nuevo estado sobre la entidad
         * gestionada por Hibernate.
         */
        entity.updateFromDomain(loanApplication);
    }

    @Override
    public Optional<LoanApplication> findById(UUID loanApplicationId) {

        return repository
                .findByIdOptional(loanApplicationId)
                .map(LoanApplicationEntity::toDomain);
    }

    @Override
    public Optional<LoanApplication> findByIdForUpdate(UUID loanApplicationId) {
        return Optional.ofNullable(repository.findById(loanApplicationId, LockModeType.PESSIMISTIC_WRITE))
                .map(LoanApplicationEntity::toDomain);
    }
}
