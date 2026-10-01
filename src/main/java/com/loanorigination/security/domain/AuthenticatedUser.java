package com.loanorigination.security.domain;

import java.util.Set;

/**
 * Representa la identidad autenticada que la capa de aplicación necesita.
 *
 * El dominio/aplicación no debe depender de JsonWebToken, SecurityIdentity
 * ni de ningún proveedor OIDC concreto.
 */
public record AuthenticatedUser(
        String externalIdentityId,
        Set<String> roles
) {

    public boolean hasRole(ApplicationRole role) {
        return roles.contains(role.name());
    }
}