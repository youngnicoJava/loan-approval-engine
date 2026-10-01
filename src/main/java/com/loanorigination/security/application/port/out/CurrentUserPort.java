package com.loanorigination.security.application.port.out;

import com.loanorigination.security.domain.AuthenticatedUser;

/**
 * Puerto que permite a la aplicación consultar quién está autenticado.
 *
 * La implementación concreta puede usar OIDC, pero la aplicación
 * solamente conoce este contrato.
 */
public interface CurrentUserPort {

    AuthenticatedUser getCurrentUser();
}