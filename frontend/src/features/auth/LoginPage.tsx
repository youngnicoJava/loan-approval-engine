import { useAuth } from 'react-oidc-context'
import { Brand } from '../../shared/components/Brand'
import { Notice } from '../../shared/components/Primitives'
import { hasOidcConfig } from '../../app/auth'
import styles from './LoginPage.module.css'

export function LoginPage() {
  const auth = useAuth()
  if (!hasOidcConfig()) return <main className={styles.page}><section className={styles.card}><Brand /><div className={styles.eyebrow}>PORTAL DE PRÉSTAMOS</div><h1>Una forma clara de financiar lo que sigue.</h1><p>Ingresá con la identidad OIDC configurada para esta aplicación.</p><Notice>Falta configurar <code>VITE_OIDC_AUTHORITY</code> y <code>VITE_OIDC_CLIENT_ID</code>. No hay usuarios ni contraseñas de demostración.</Notice><a className={styles.button} href="/">Volver al inicio</a></section></main>
  return <main className={styles.page}><section className={styles.card}><Brand /><div className={styles.eyebrow}>PORTAL DE PRÉSTAMOS</div><h1>Una forma clara de financiar lo que sigue.</h1><p>Iniciá sesión de forma segura con tu proveedor.</p>{auth.error && <Notice>{auth.error.message}</Notice>}<button className={styles.button} onClick={() => void auth.signinRedirect()} disabled={auth.isLoading}>{auth.isLoading ? 'Conectando…' : 'Continuar con inicio de sesión'}</button></section></main>
}
