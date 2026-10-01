package com.loanorigination.customer.adapter.out.persistence;

import com.loanorigination.customer.domain.Customer;
import com.loanorigination.customer.domain.CustomerStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Modelo persistente de Customer.
 *
 * JPA/Panache se mantienen fuera del dominio.
 */
@Entity
@Table(name = "customers")
public class CustomerEntity extends PanacheEntityBase {

    @Id
    private UUID id;

    @Column(
            name = "external_identity_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String externalIdentityId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 320)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CustomerEntity() {
        // Constructor requerido por JPA.
    }

    public static CustomerEntity fromDomain(Customer customer) {

        CustomerEntity entity = new CustomerEntity();

        entity.id = customer.id();
        entity.updateFromDomain(customer);

        return entity;
    }

    /**
     * La entidad persistente refleja el estado validado por el agregado.
     * No decide por sí misma si una transición ACTIVE/BLOCKED es válida.
     */
    public void updateFromDomain(Customer customer) {

        this.externalIdentityId = customer.externalIdentityId();
        this.fullName = customer.fullName();
        this.email = customer.email();
        this.status = customer.status();
        this.createdAt = customer.createdAt();
    }

    public Customer toDomain() {

        return Customer.restore(
                id,
                externalIdentityId,
                fullName,
                email,
                status,
                createdAt
        );
    }
}