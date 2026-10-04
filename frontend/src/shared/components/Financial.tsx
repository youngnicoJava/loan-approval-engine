import type { ReactNode } from 'react'
import styles from './Financial.module.css'

export function Money({ value, currency }: { value: number; currency: string }) {
  return <span>{new Intl.NumberFormat('es-AR', { style: 'currency', currency, minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value)}</span>
}

export function Rate({ value }: { value: number }) { return <span>{new Intl.NumberFormat('es-AR', { maximumFractionDigits: 4 }).format(value)}% TNA</span> }
export function Term({ months }: { months: number }) { return <span>{months} {months === 1 ? 'mes' : 'meses'}</span> }
export function DateTime({ value, dateOnly = false }: { value: string | null | undefined; dateOnly?: boolean }) {
  if (!value) return <span>—</span>
  return <time dateTime={value}>{new Intl.DateTimeFormat('es-AR', dateOnly ? { dateStyle: 'medium', timeZone: 'UTC' } : { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value))}</time>
}

export function PageHeader({ eyebrow, title, description, action }: { eyebrow: string; title: string; description?: string; action?: ReactNode }) {
  return <header className={styles.header}><div><div className={styles.eyebrow}>{eyebrow}</div><h1>{title}</h1>{description && <p>{description}</p>}</div>{action && <div>{action}</div>}</header>
}

export function FinancialGrid({ children }: { children: ReactNode }) { return <div className={styles.grid}>{children}</div> }
export function FinancialValue({ label, children }: { label: string; children: ReactNode }) { return <div className={styles.item}><span>{label}</span><strong>{children}</strong></div> }
