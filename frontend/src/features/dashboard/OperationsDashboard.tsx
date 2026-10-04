import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { Loan, LoanApplication } from '../../shared/types/api'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { Money, PageHeader, Term } from '../../shared/components/Financial'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import styles from './DashboardPage.module.css'
import { rolesFromProfile } from '../../shared/auth/roles'

function useOperationsData<T>(load: (token: string) => Promise<T>) {
  const token = useAuth().user?.access_token
  const [value, setValue] = useState<T | null>(null)
  const [error, setError] = useState('')
  useEffect(() => { if (!token) return; let active = true; load(token).then((data) => { if (active) setValue(data) }).catch((reason: unknown) => { if (active) setError(errorMessage(reason)) }); return () => { active = false } }, [token, load])
  return { value, error }
}

export function OperationsDashboard({ admin = false }: { admin?: boolean }) {
  const roles = rolesFromProfile(useAuth().user?.profile)
  const loadApplications = useCallback((token: string) => api.applicationQueue(token, null, 0, 100), [])
  const loadLoans = useCallback((token: string) => api.operationalLoans(token, 'PENDING_DISBURSEMENT'), [])
  const applicationsData = useOperationsData(loadApplications)
  const loansData = useOperationsData(loadLoans)
  const applications = (applicationsData.value as LoanApplication[] | null) ?? null
  const loans = (loansData.value as Loan[] | null) ?? null
  const submitted = applications?.filter((item) => item.status === 'SUBMITTED') ?? []
  const manualReview = applications?.filter((item) => item.status === 'UNDER_REVIEW') ?? []
  const approved = applications?.filter((item) => item.status === 'APPROVED') ?? []
  return <Shell role={roles.includes('ADMIN') ? 'ADMIN' : 'LOAN_OFFICER'}><PageHeader eyebrow={admin ? 'ADMINISTRACIÓN' : 'OPERACIONES'} title={admin ? 'Panel de operaciones' : 'Consola de originación'} description="Colas de trabajo según el estado actual de cada agregado." action={admin ? <Link className={styles.link} to="/audit">Abrir auditoría →</Link> : undefined} />
    <div className={styles.summary}><Summary label="Enviadas" value={applications ? submitted.length : '—'} detail="Esperan evaluación" /><Summary label="Revisión manual" value={applications ? manualReview.length : '—'} detail="Casos bajo revisión" /><Summary label="Pendientes de desembolso" value={loans?.length ?? '—'} detail="Préstamos listos para operar" /></div>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Aplicaciones enviadas</h2><p>La evaluación sólo está disponible para solicitudes SUBMITTED</p></div><Link className={styles.link} to="/applications">Abrir cola →</Link></div>{applications ? submitted.length ? <ApplicationRows items={submitted.slice(0, 5)} /> : <Empty title="No hay solicitudes enviadas" text="Las nuevas presentaciones aparecerán aquí." /> : <LoadingError error={applicationsData.error} />}</section>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Revisión manual</h2><p>Las evaluaciones REFER mantienen la solicitud UNDER_REVIEW</p></div><Link className={styles.link} to="/applications?status=UNDER_REVIEW">Ver casos →</Link></div>{applications ? manualReview.length ? <ApplicationRows items={manualReview.slice(0, 5)} /> : <Empty title="No hay casos en revisión manual" text="No hay solicitudes UNDER_REVIEW en la cola actual." /> : <LoadingError error={applicationsData.error} />}</section>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Aprobadas · oferta pendiente</h2><p>Revisá cada detalle para emitir una oferta</p></div><Link className={styles.link} to="/applications?status=APPROVED">Ver aprobadas →</Link></div>{applications ? approved.length ? <ApplicationRows items={approved.slice(0, 5)} /> : <Empty title="No hay solicitudes aprobadas" text="Las solicitudes aprobadas aparecerán aquí." /> : <LoadingError error={applicationsData.error} />}</section>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Préstamos por desembolsar</h2><p>El desembolso usa el adapter local disponible en el backend</p></div><Link className={styles.link} to="/loans?status=PENDING_DISBURSEMENT">Ver préstamos →</Link></div>{loans ? loans.length ? <LoanRows items={loans.slice(0, 5)} /> : <Empty title="No hay desembolsos pendientes" text="Los préstamos pendientes se mostrarán aquí." /> : <LoadingError error={loansData.error} />}</section>
  </Shell>
}

function Summary({ label, value, detail }: { label: string; value: number | string; detail: string }) { return <div className={styles.summaryCard}><span>{label}</span><strong>{value}</strong><small>{detail}</small></div> }
function ApplicationRows({ items }: { items: LoanApplication[] }) { return <div className={styles.list}>{items.map((application) => <article key={application.id} className={styles.item}><div className={styles.icon}>⌂</div><div className={styles.main}><div className={styles.title}><h3>{application.productType.replaceAll('_', ' ')}</h3><StatusBadge value={application.status} /></div><p><Money value={application.requestedAmount} currency={application.currency} /> · <Term months={application.termMonths} /> · {new Date(application.updatedAt).toLocaleDateString('es-AR')}</p></div><Link className={styles.link} to={`/applications/${application.id}`}>Abrir →</Link></article>)}</div> }
function LoanRows({ items }: { items: Loan[] }) { return <div className={styles.list}>{items.map((loan) => <article key={loan.id} className={styles.item}><div className={styles.icon}>↗</div><div className={styles.main}><div className={styles.title}><h3><Money value={loan.principal} currency={loan.currency} /></h3><StatusBadge value={loan.status} /></div><p><Term months={loan.termMonths} /></p></div><Link className={styles.link} to={`/loans/${loan.id}`}>Desembolso →</Link></article>)}</div> }
