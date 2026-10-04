import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { AuditEvent } from '../../shared/types/api'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { DateTime, PageHeader } from '../../shared/components/Financial'
import { Shell } from '../../shared/components/Shell'
import styles from './DashboardPage.module.css'
import { rolesFromProfile } from '../../shared/auth/roles'

export function AuditorDashboard() {
  const auth = useAuth()
  const token = auth.user?.access_token
  const role = rolesFromProfile(auth.user?.profile).includes('ADMIN') ? 'ADMIN' : 'AUDITOR'
  const load = useCallback(() => token ? api.audit(token, 0, 10) : Promise.resolve([]), [token])
  const [events, setEvents] = useState<AuditEvent[] | null>(null)
  const [error, setError] = useState('')
  useEffect(() => { let active = true; load().then((data) => { if (active) setEvents(data) }).catch((reason: unknown) => { if (active) setError(errorMessage(reason)) }); return () => { active = false } }, [load])
  return <Shell role={role}><PageHeader eyebrow="AUDITORÍA" title="Actividad reciente" description="Trazabilidad funcional del flujo de originación." action={<Link className={styles.link} to="/audit">Abrir explorador →</Link>} /><section className={styles.section}><div className={styles.sectionHeading}><div><h2>Últimos eventos</h2><p>Consulta de sólo lectura</p></div></div>{events ? events.length ? <div className={styles.list}>{events.map((event) => <article key={event.id} className={styles.item}><div className={styles.icon}>◎</div><div className={styles.main}><div className={styles.title}><h3>{event.action.replaceAll('_', ' ')}</h3><span className={styles.badge}>{event.aggregateType}</span></div><p><DateTime value={event.occurredAt} /> · {event.actorType} · {event.actorId}</p></div><Link className={styles.link} to={`/audit?aggregateId=${event.aggregateId}`}>Rastrear →</Link></article>)}</div> : <Empty title="No hay actividad de auditoría" text="Los eventos funcionales aparecerán al ejecutar el flujo." /> : <LoadingError error={error} />}</section></Shell>
}
