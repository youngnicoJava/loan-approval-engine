package com.loanorigination.customer.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root del cliente dentro del bounded context de Loan Origination.
 *
 * Customer representa la identidad funcional utilizada por el proceso
 * de originación. La identidad técnica/externalIdentityId permite vincular
 * posteriormente este registro con el proveedor de identidad (OIDC/JWT).
 *
 * El agregado protege las transiciones ACTIVE <-> BLOCKED.
 */
public final class Customer {

    private final UUID id;
    private final String externalIdentityId;
    private final String fullName;
    private final String email;
    private final Instant createdAt;

    private CustomerStatus status;

    private Customer(
            UUID id,
            String externalIdentityId,
            String fullName,
            String email,
            CustomerStatus status,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.externalIdentityId = requireExternalIdentityId(externalIdentityId);
        this.fullName = requireFullName(fullName);
        this.email = normalizeEmail(email);
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    /**
     * Los nuevos clientes siempre comienzan ACTIVE.
     *
     * La identidad externalIdentityId se conserva como referencia al
     * sistema de identidad externo; todavía no estamos acoplando el dominio
     * a OIDC ni a ningún proveedor concreto.
     */
    public static Customer create(
            String externalIdentityId,
            String fullName,
            String email,
            Instant now
    ) {
        Objects.requireNonNull(now, "now cannot be null");

        return new Customer(
                UUID.randomUUID(),
                externalIdentityId,
                fullName,
                email,
                CustomerStatus.ACTIVE,
                now
        );
    }

    /**
     * Reconstruye el agregado a partir del estado persistido.
     *
     * El adapter de persistencia puede rehidratar el dominio sin exponer
     * setters ni permitir mutaciones arbitrarias desde infraestructura.
     */
    public static Customer restore(
            UUID id,
            String externalIdentityId,
            String fullName,
            String email,
            CustomerStatus status,
            Instant createdAt
    ) {
        return new Customer(
                id,
                externalIdentityId,
                fullName,
                email,
                status,
                createdAt
        );
    }

    /**
     * Un cliente ACTIVE puede pasar a BLOCKED.
     *
     * Un cliente ya bloqueado no puede volver a bloquearse porque eso
     * representaría una transición inválida del ciclo de vida.
     */
    public void block() {

        if (status != CustomerStatus.ACTIVE) {
            throw new InvalidCustomerStateException(
                    "Customer cannot be blocked from status " + status
            );
        }

        this.status = CustomerStatus.BLOCKED;
    }

    /**
     * Un cliente BLOCKED puede volver a ACTIVE.
     */
    public void activate() {

        if (status != CustomerStatus.BLOCKED) {
            throw new InvalidCustomerStateException(
                    "Customer cannot be activated from status " + status
            );
        }

        this.status = CustomerStatus.ACTIVE;
    }

    private static String requireExternalIdentityId(String value) {

        Objects.requireNonNull(value, "externalIdentityId cannot be null");

        String normalized = value.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    "externalIdentityId cannot be blank"
            );
        }

        if (normalized.length() > 255) {
            throw new IllegalArgumentException(
                    "externalIdentityId cannot exceed 255 characters"
            );
        }

        return normalized;
    }

    private static String requireFullName(String value) {

        Objects.requireNonNull(value, "fullName cannot be null");

        String normalized = value.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    "fullName cannot be blank"
            );
        }

        if (normalized.length() > 150) {
            throw new IllegalArgumentException(
                    "fullName cannot exceed 150 characters"
            );
        }

        return normalized;
    }

    private static String normalizeEmail(String value) {

        Objects.requireNonNull(value, "email cannot be null");

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    "email cannot be blank"
            );
        }

        if (normalized.length() > 320) {
            throw new IllegalArgumentException(
                    "email cannot exceed 320 characters"
            );
        }

        return normalized;
    }

    public UUID id() {
        return id;
    }

    public String externalIdentityId() {
        return externalIdentityId;
    }

    public String fullName() {
        return fullName;
    }

    public String email() {
        return email;
    }

    public CustomerStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }
}