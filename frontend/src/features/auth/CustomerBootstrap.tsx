import { useEffect, useRef, useState } from 'react'
import { useAuth } from 'react-oidc-context'
import { api, ApiRequestError, errorMessage } from '../../shared/api/client'
import styles from './CustomerBootstrap.module.css'
import { rolesFromProfile } from '../../shared/auth/roles'

type Props = { children: React.ReactNode }
type BootstrapState = { token: string; status: 'ready' } | { token: string; status: 'error'; message: string }

export function CustomerBootstrap({ children }: Props) {
  const auth = useAuth()
  const token = auth.user?.access_token
  const profile = auth.user?.profile
  const [state, setState] = useState<BootstrapState | null>(null)
  const [attempt, setAttempt] = useState(0)
  const inFlight = useRef(new Map<string, Promise<void>>())
  const completed = useRef(new Set<string>())

  useEffect(() => {
    if (!token || completed.current.has(token)) return
    const pending = inFlight.current.get(token)
    if (pending) return

    const bootstrap = (async () => {
      try {
        await api.currentCustomer(token)
      } catch (error) {
        if (!(error instanceof ApiRequestError && error.status === 404 && error.code === 'CUSTOMER_NOT_FOUND')) {
          throw error
        }

        const fullName = typeof profile?.name === 'string' ? profile.name.trim() : ''
        const email = typeof profile?.email === 'string' ? profile.email.trim() : ''
        if (!fullName || !email) {
          throw new Error('Tu perfil de identidad no incluye nombre y correo. Actualizá esos datos en el proveedor de acceso y volvé a iniciar sesión.')
        }
        await api.createCustomer(token, { fullName, email })
      }

      completed.current.add(token)
      setState({ token, status: 'ready' })
    })()

    inFlight.current.set(token, bootstrap)
    bootstrap.catch((error: unknown) => {
      setState({ token, status: 'error', message: errorMessage(error) })
    }).finally(() => {
      inFlight.current.delete(token)
    })
  }, [token, profile?.name, profile?.email, attempt])

  if (!token || !rolesFromProfile(profile).includes('CUSTOMER')) return <>{children}</>
  if (!state || state.token !== token) return <main className={styles.state}><div className={styles.panel}>Preparando tu cuenta…</div></main>
  if (state.status === 'error') return <main className={styles.state}><section className={styles.panel}><h1>No pudimos preparar tu cuenta</h1><p>{state.message}</p><button className={styles.retry} onClick={() => { setState(null); completed.current.delete(token); setAttempt((value) => value + 1) }}>Reintentar</button></section></main>
  return <>{children}</>
}
