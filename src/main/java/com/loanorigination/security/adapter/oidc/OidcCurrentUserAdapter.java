package com.loanorigination.security.adapter.oidc;

import com.loanorigination.security.application.port.out.CurrentUserPort;
import com.loanorigination.security.domain.AuthenticatedUser;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

/**
 * Adapta la identidad verificada por Quarkus/OIDC al modelo
 * que utiliza nuestra capa de aplicación.
 *
 * De esta manera los casos de uso no quedan acoplados al framework
 * de seguridad ni al proveedor de identidad.
 */
@ApplicationScoped
public class OidcCurrentUserAdapter implements CurrentUserPort {

    private final SecurityIdentity securityIdentity;
    private final JsonWebToken accessToken;

    @Inject
    public OidcCurrentUserAdapter(
            SecurityIdentity securityIdentity,
            JsonWebToken accessToken
    ) {
        this.securityIdentity = securityIdentity;
        this.accessToken = accessToken;
    }

    @Override
    public AuthenticatedUser getCurrentUser() {

        if (securityIdentity.isAnonymous()) {
            throw new IllegalStateException(
                    "No authenticated user is available"
            );
        }

        return new AuthenticatedUser(
                accessToken.getSubject(),
                Set.copyOf(securityIdentity.getRoles())
        );
    }
}