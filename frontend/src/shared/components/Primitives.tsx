import styles from './Primitives.module.css'

export function Notice({ children }: { children: React.ReactNode }) { return <div className={styles.notice}>{children}</div> }
export function Empty({ title, text }: { title: string; text: string }) { return <div className={styles.empty}><span className={styles.icon}>○</span><strong>{title}</strong><p>{text}</p></div> }
export function LoadingError({ error }: { error: string }) { return <div className={`${styles.empty} ${styles.panel}`}>{error || 'Cargando información…'}</div> }
export function Detail({ label, value }: { label: string; value: string }) { return <div className={styles.detail}><span>{label}</span><strong>{value}</strong></div> }

export function money(value: number, currency: string) { return new Intl.NumberFormat('es-AR', { style: 'currency', currency, maximumFractionDigits: 0 }).format(value) }
export function statusName(value: string) { return value.replaceAll('_', ' ').toLowerCase().replace(/^\w/, (letter) => letter.toUpperCase()) }
