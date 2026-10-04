import type { ReactNode } from 'react'
import { NavLink } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { rolesFromProfile, type AppRole } from '../auth/roles'
import { Brand } from './Brand'
import styles from './Shell.module.css'

export function Shell({ children, role }: { children: ReactNode; role?: AppRole }) {
  const auth = useAuth()
  const roles = rolesFromProfile(auth.user?.profile)
  const activeRole = role ?? (roles.includes('ADMIN') ? 'ADMIN' : roles[0] ?? 'CUSTOMER')
  const nav: { label: string; to: string }[] = [{ label: 'Inicio', to: '/dashboard' }]
  if (activeRole === 'CUSTOMER') nav.push({ label: 'Solicitudes', to: '/applications' }, { label: 'Ofertas', to: '/offers' }, { label: 'Préstamos', to: '/loans' })
  if (activeRole === 'LOAN_OFFICER' || activeRole === 'ADMIN') nav.push({ label: 'Solicitudes', to: '/applications' }, { label: 'Préstamos', to: '/loans' })
  if (activeRole === 'AUDITOR' || activeRole === 'ADMIN') nav.push({ label: 'Auditoría', to: '/audit' })
  return <div className={styles.layout}><aside className={styles.sidebar}><Brand /><div className={styles.label}>{activeRole.replaceAll('_', ' ')}</div><nav className={styles.navigation}>{nav.map((item) => <NavLink key={item.to} end={item.to === '/dashboard'} to={item.to}>{item.label}</NavLink>)}</nav><div className={styles.footer}><div className={styles.avatar}>{auth.user?.profile.preferred_username?.toString().slice(0, 1).toUpperCase() ?? 'U'}</div><div className={styles.user}><span>{auth.user?.profile.preferred_username?.toString() ?? 'Usuario'}</span><small>{activeRole.replaceAll('_', ' ')}</small></div><button onClick={() => void auth.removeUser()}>Salir</button></div></aside><main className={styles.content}>{children}</main></div>
}
