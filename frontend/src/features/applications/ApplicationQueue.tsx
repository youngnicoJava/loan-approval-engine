import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { LoanApplication, LoanApplicationStatus } from '../../shared/types/api'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { Money, PageHeader, Term } from '../../shared/components/Financial'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import { rolesFromProfile } from '../../shared/auth/roles'
import styles from './ApplicationQueue.module.css'

const statuses: LoanApplicationStatus[] = ['SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'DRAFT', 'CANCELLED']

export function ApplicationQueue() {
  const auth = useAuth()
  const token = auth.user?.access_token ?? ''
  const role = rolesFromProfile(auth.user?.profile).includes('ADMIN') ? 'ADMIN' : 'LOAN_OFFICER'
  const [params, setParams] = useSearchParams()
  const status = (params.get('status') || '') as LoanApplicationStatus | ''
  const [items, setItems] = useState<LoanApplication[] | null>(null)
  const [error, setError] = useState('')
  const [offset, setOffset] = useState(0)
  const limit = 25
  useEffect(() => { if (!token) return; let active = true; setItems(null); setError(''); api.applicationQueue(token, status || null, offset, limit).then((data) => { if (active) setItems(data) }).catch((reason: unknown) => { if (active) setError(errorMessage(reason)) }); return () => { active = false } }, [token, status, offset])
  const chooseStatus = (value: string) => { setParams(value ? { status: value } : {}); setOffset(0) }
  return <Shell role={role}><PageHeader eyebrow="OPERACIONES" title="Cola de solicitudes" description="Revisá el estado de cada solicitud y avanzá sólo con acciones habilitadas." action={<label className={styles.filter}>Filtrar por estado<select value={status} onChange={(event) => chooseStatus(event.target.value)}><option value="">Todos los estados</option>{statuses.map((value) => <option key={value} value={value}>{value.replaceAll('_', ' ')}</option>)}</select></label>} />
    {items ? items.length ? <div className={styles.rows}>{items.map((item) => <article className={styles.row} key={item.id}><div className={styles.product}>⌂</div><div className={styles.primary}><div className={styles.title}><h2>{item.productType.replaceAll('_', ' ')}</h2><StatusBadge value={item.status} /></div><p><Money value={item.requestedAmount} currency={item.currency} /> · <Term months={item.termMonths} /> · {new Date(item.updatedAt).toLocaleDateString('es-AR')}</p><Link className={styles.customer} to={`/customers/${item.customerId}`}>Abrir perfil de cliente →</Link></div><Link className={styles.open} to={`/applications/${item.id}`}>Abrir caso <span>→</span></Link></article>)}</div> : <Empty title="No hay solicitudes para este filtro" text="Probá otro estado o volvé cuando lleguen nuevas solicitudes." /> : <LoadingError error={error} />}
    <div className={styles.pagination}><button disabled={offset === 0} onClick={() => setOffset(Math.max(0, offset - limit))}>← Anterior</button><span>Filas {offset + 1}–{offset + (items?.length ?? 0)}</span><button disabled={!items || items.length < limit} onClick={() => setOffset(offset + limit)}>Siguiente →</button></div>
  </Shell>
}
