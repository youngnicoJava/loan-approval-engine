import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, ApiRequestError, errorMessage } from '../../shared/api/client'
import type { Customer, LoanApplication, LoanOffer, RiskAssessment } from '../../shared/types/api'
import { DateTime, FinancialValue, FinancialGrid, Money, PageHeader, Rate, Term } from '../../shared/components/Financial'
import { LoadingError, Notice } from '../../shared/components/Primitives'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { ConfirmDialog } from '../../shared/components/ConfirmDialog'
import { Shell } from '../../shared/components/Shell'
import { rolesFromProfile } from '../../shared/auth/roles'
import styles from './ApplicationPage.module.css'

type Confirmation = { kind: 'approve' | 'reject' | 'evaluate'; title: string; description: string; label: string; danger?: boolean }

export function ApplicationPage() {
  const { id = '' } = useParams()
  const auth = useAuth()
  const token = auth.user?.access_token ?? ''
  const roles = rolesFromProfile(auth.user?.profile)
  const customerRole = roles.includes('CUSTOMER')
  const operator = roles.includes('LOAN_OFFICER') || roles.includes('ADMIN')
  const auditor = roles.includes('AUDITOR')
  const [application, setApplication] = useState<LoanApplication | null>(null)
  const [customer, setCustomer] = useState<Customer | null>(null)
  const [risk, setRisk] = useState<RiskAssessment | null>(null)
  const [offer, setOffer] = useState<LoanOffer | null>(null)
  const [loadError, setLoadError] = useState('')
  const [message, setMessage] = useState('')
  const [actionError, setActionError] = useState('')
  const [reload, setReload] = useState(0)
  const [busy, setBusy] = useState(false)
  const [confirmation, setConfirmation] = useState<Confirmation | null>(null)
  const [principal, setPrincipal] = useState('')
  const [termMonths, setTermMonths] = useState('')
  const [rate, setRate] = useState('')
  const [expiresAt, setExpiresAt] = useState(() => new Date(Date.now() + 7 * 86400000 - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16))

  const load = useCallback(() => api.applications(token, id), [token, id])
  useEffect(() => { if (!token || !id) return; let active = true; setApplication(null); load().then((data) => { if (active) { setApplication(data); setPrincipal(String(data.requestedAmount)); setTermMonths(String(data.termMonths)); setLoadError('') } }).catch((reason: unknown) => { if (active) setLoadError(errorMessage(reason)) }); return () => { active = false } }, [load, reload])
  useEffect(() => {
    if (!token || !application) return
    let active = true
    if (operator || auditor) {
      api.riskAssessment(token, id).then((value) => { if (active) setRisk(value) }).catch(() => { if (active) setRisk(null) })
      api.offerForApplication(token, id).then((value) => { if (active) setOffer(value) }).catch((error: unknown) => { if (active && !(error instanceof ApiRequestError && error.status === 404)) setActionError(errorMessage(error)) })
      api.customer(token, application.customerId).then((value) => { if (active) setCustomer(value) }).catch(() => { if (active) setCustomer(null) })
    } else if (customerRole) {
      api.offers(token).then((values) => { if (active) setOffer(values.find((value) => value.loanApplicationId === id) ?? null) }).catch(() => { if (active) setOffer(null) })
    }
    return () => { active = false }
  }, [token, id, application, operator, auditor, customerRole])

  const runConfirmed = async () => {
    if (!confirmation) return
    const action = confirmation.kind
    setBusy(true); setActionError(''); setMessage('')
    try {
      if (action === 'evaluate') {
        const value = await api.evaluateApplication(token, id)
        if ('decision' in value) {
          setRisk(value)
          setMessage(`Evaluación completada: ${value.decision}.`)
        } else {
          setRisk(null)
          setMessage(value.message)
          setReload((current) => current + 1)
        }
      }
      if (action === 'approve') { await api.approveApplication(token, id); setMessage('Solicitud aprobada.') }
      if (action === 'reject') { await api.rejectApplication(token, id); setMessage('Solicitud rechazada.') }
      if (action !== 'evaluate') setReload((value) => value + 1)
    } catch (error) { setActionError(errorMessage(error)) }
    finally { setBusy(false); setConfirmation(null) }
  }

  const issueOffer = async (event: React.FormEvent) => {
    event.preventDefault(); setBusy(true); setActionError(''); setMessage('')
    try {
      const value = await api.issueOffer(token, id, { offeredPrincipal: Number(principal), offeredTermMonths: Number(termMonths), annualInterestRatePercentage: Number(rate), expiresAt: new Date(expiresAt).toISOString() })
      setOffer(value); setMessage('Oferta emitida. Los importes calculados fueron devueltos por el backend.')
    } catch (error) { setActionError(errorMessage(error)) }
    finally { setBusy(false) }
  }

  const canEvaluate = operator && application?.status === 'SUBMITTED'
  const canManualResolve = operator && application?.status === 'UNDER_REVIEW' && risk?.decision === 'REFER'
  const canIssue = operator && application?.status === 'APPROVED' && (!offer || offer.status === 'EXPIRED' || offer.status === 'DECLINED')

  if (!application) return <Shell><PageHeader eyebrow="SOLICITUD" title="Detalle de solicitud" />{loadError ? <LoadingError error={loadError} /> : <div className={styles.loading}>Cargando información de la solicitud…</div>}</Shell>

  return <Shell><PageHeader eyebrow={operator || auditor ? 'CONSOLA DE ORIGINACIÓN' : 'PORTAL DEL CLIENTE'} title={application.productType.replaceAll('_', ' ')} description={`Solicitud ${application.id}`} action={<StatusBadge value={application.status} />} />
    <div className={styles.layout}><div className={styles.mainColumn}>
      <section className={styles.card}><h2>Datos de la solicitud</h2><FinancialGrid><FinancialValue label="Monto solicitado"><Money value={application.requestedAmount} currency={application.currency} /></FinancialValue><FinancialValue label="Plazo"><Term months={application.termMonths} /></FinancialValue><FinancialValue label="Producto">{application.productType.replaceAll('_', ' ')}</FinancialValue><FinancialValue label="Estado"><StatusBadge value={application.status} /></FinancialValue><FinancialValue label="Destino">{application.purpose}</FinancialValue><FinancialValue label="Creada"><DateTime value={application.createdAt} /></FinancialValue><FinancialValue label="Enviada"><DateTime value={application.submittedAt} /></FinancialValue><FinancialValue label="Actualizada"><DateTime value={application.updatedAt} /></FinancialValue></FinancialGrid></section>
      {(operator || auditor) && <section className={styles.card}><div className={styles.sectionTitle}><div><h2>Cliente</h2><p>Identidad asociada por la API</p></div>{customer && <StatusBadge value={customer.status} />}</div>{customer ? <div className={styles.customer}><strong>{customer.fullName}</strong><span>{customer.email}</span><Link to={`/customers/${customer.id}`}>Administrar perfil →</Link></div> : <div className={styles.muted}>Cliente {application.customerId}</div>}</section>}
      {(operator || auditor) && <section className={styles.card}><h2>Evaluación de riesgo</h2>{risk ? <><div className={styles.risk}><StatusBadge value={risk.decision} /><span>{risk.reasonCode.replaceAll('_', ' ')}</span></div><FinancialGrid><FinancialValue label="Fuente">{risk.source.replaceAll('_', ' ')}</FinancialValue><FinancialValue label="Evaluada"><DateTime value={risk.assessedAt} /></FinancialValue></FinancialGrid></> : <p className={styles.muted}>Todavía no hay una evaluación guardada.</p>}</section>}
      <section className={styles.card}><div className={styles.sectionTitle}><div><h2>Oferta</h2><p>Condiciones devueltas por el motor financiero del backend</p></div>{offer && <StatusBadge value={offer.status} />}</div>{offer ? <><FinancialGrid><FinancialValue label="Capital"><Money value={offer.principal} currency={offer.currency} /></FinancialValue><FinancialValue label="Tasa"><Rate value={offer.annualInterestRatePercentage} /></FinancialValue><FinancialValue label="Plazo"><Term months={offer.termMonths} /></FinancialValue><FinancialValue label="Cuota mensual"><Money value={offer.monthlyInstallment} currency={offer.currency} /></FinancialValue><FinancialValue label="Total a devolver"><Money value={offer.totalRepayment} currency={offer.currency} /></FinancialValue><FinancialValue label="Vence"><DateTime value={offer.expiresAt} /></FinancialValue></FinancialGrid>{customerRole && offer.status === 'PENDING' && <Link className={styles.actionLink} to={`/offers/${offer.id}`}>Revisar y responder oferta →</Link>}</> : <p className={styles.muted}>No hay una oferta asociada todavía.</p>}</section>
      {message && <Notice>{message}</Notice>}{actionError && <Notice>{actionError}</Notice>}
      {canIssue && <section className={styles.card}><h2>Emitir oferta</h2><p className={styles.muted}>Elegí los términos. Cuota y total serán calculados por el backend.</p><form className={styles.form} onSubmit={(event) => void issueOffer(event)}><label>Capital ofertado<input type="number" min="0.01" step="0.01" value={principal} onChange={(event) => setPrincipal(event.target.value)} required /></label><label>Plazo en meses<input type="number" min="1" value={termMonths} onChange={(event) => setTermMonths(event.target.value)} required /></label><label>Tasa nominal anual (%)<input type="number" min="0" step="0.0001" value={rate} onChange={(event) => setRate(event.target.value)} required /></label><label>Vencimiento<input type="datetime-local" value={expiresAt} onChange={(event) => setExpiresAt(event.target.value)} required /></label><button className={styles.primary} disabled={busy}>{busy ? 'Emitiendo…' : 'Emitir oferta'}</button></form></section>}
      {(canEvaluate || canManualResolve) && <section className={styles.card}><h2>Acciones de revisión</h2><p className={styles.muted}>{canEvaluate ? 'La evaluación sólo se inicia para una solicitud SUBMITTED.' : 'La decisión automática REFER permite resolución manual mientras permanece UNDER_REVIEW.'}</p><div className={styles.actions}>{canEvaluate && <button className={styles.primary} disabled={busy} onClick={() => setConfirmation({ kind: 'evaluate', title: 'Ejecutar evaluación de riesgo', description: 'La evaluación actualizará el estado de la solicitud según APPROVE, REJECT o REFER.', label: 'Evaluar riesgo' })}>Evaluar riesgo</button>}{canManualResolve && <><button className={styles.primary} disabled={busy} onClick={() => setConfirmation({ kind: 'approve', title: 'Aprobar solicitud', description: 'La solicitud quedará APPROVED y podrá recibir una oferta. Todavía no crea un préstamo.', label: 'Aprobar' })}>Aprobar</button><button className={styles.danger} disabled={busy} onClick={() => setConfirmation({ kind: 'reject', title: 'Rechazar solicitud', description: 'El proceso de originación de esta solicitud finalizará como REJECTED.', label: 'Rechazar', danger: true })}>Rechazar</button></>}</div></section>}
    </div><aside className={styles.sideColumn}><section className={styles.card}><h2>Resumen operativo</h2><div className={styles.sideRow}><span>Estado actual</span><StatusBadge value={application.status} /></div><div className={styles.sideRow}><span>Solicitud enviada</span><strong>{application.submittedAt ? 'Sí' : 'No'}</strong></div>{risk && <div className={styles.sideRow}><span>Decisión de riesgo</span><StatusBadge value={risk.decision} /></div>}{offer && <div className={styles.sideRow}><span>Oferta</span><StatusBadge value={offer.status} /></div>}</section><section className={styles.card}><h2>Historial</h2><div className={styles.timeline}><div><i /><span>Creada</span><DateTime value={application.createdAt} /></div><div><i /><span>Enviada</span><DateTime value={application.submittedAt} /></div><div><i /><span>Último cambio</span><DateTime value={application.updatedAt} /></div></div></section></aside></div>
    <ConfirmDialog open={Boolean(confirmation)} title={confirmation?.title ?? ''} description={confirmation?.description ?? ''} confirmLabel={confirmation?.label ?? 'Confirmar'} danger={confirmation?.danger} busy={busy} onCancel={() => setConfirmation(null)} onConfirm={() => void runConfirmed()} />
  </Shell>
}
