import type { UserProfile } from 'oidc-client-ts'

export type AppRole = 'CUSTOMER' | 'LOAN_OFFICER' | 'AUDITOR' | 'ADMIN'
const appRoles: AppRole[] = ['CUSTOMER', 'LOAN_OFFICER', 'AUDITOR', 'ADMIN']

export function rolesFromProfile(profile: UserProfile | undefined): AppRole[] {
  const realmAccess = profile?.realm_access as { roles?: unknown } | undefined
  if (!Array.isArray(realmAccess?.roles)) return []
  const roles: unknown[] = realmAccess.roles
  return appRoles.filter((role) => roles.includes(role))
}

export function hasAnyRole(roles: AppRole[], allowed: AppRole[]): boolean {
  return roles.some((role) => allowed.includes(role))
}
