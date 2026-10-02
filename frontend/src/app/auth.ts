import type { AuthProviderProps } from 'react-oidc-context'

export function oidcConfig(): AuthProviderProps {
  const authority = import.meta.env.VITE_OIDC_AUTHORITY
  const clientId = import.meta.env.VITE_OIDC_CLIENT_ID

  return {
    authority: authority ?? window.location.origin,
    client_id: clientId ?? 'oidc-not-configured',
    redirect_uri:
        import.meta.env.VITE_OIDC_REDIRECT_URI ??
        `${window.location.origin}/login`,
    post_logout_redirect_uri:
        import.meta.env.VITE_OIDC_POST_LOGOUT_REDIRECT_URI ??
        `${window.location.origin}/login`,
    response_type: 'code',
    scope: 'openid profile email',
    onSigninCallback: () =>
        window.history.replaceState(
            {},
            document.title,
            window.location.pathname
        ),
  }
}

export function hasOidcConfig(): boolean {
  return Boolean(
      import.meta.env.VITE_OIDC_AUTHORITY &&
      import.meta.env.VITE_OIDC_CLIENT_ID
  )
}