import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { LoanApplication } from '../../shared/types/api'
import { Empty, Notice } from '../../shared/components/Primitives'
import { Money, PageHeader, Term } from '../../shared/components/Financial'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import { LoadingError } from '../../shared/components/Primitives'
import styles from './ApplicationsPage.module.css'

export function ApplicationsPage() {
  const token = useAuth().user?.access_token ?? ''
  const [applications, setApplications] = useState<LoanApplication[] | null>(null)
  const [loadError, setLoadError] = useState('')
  const [reload, setReload] = useState(0)
  const [productType] = useState('PERSONAL_LOAN')
  const [amount, setAmount] = useState('1500000')
  const [currency, setCurrency] = useState('ARS')
  const [termMonths, setTermMonths] = useState('24')
  const [purpose, setPurpose] = useState('Gastos personales')
  const [monthlyIncome, setMonthlyIncome] = useState('1500000')
  const [existingMonthlyDebtObligations, setExistingMonthlyDebtObligations] = useState('0')
  const [employmentStatus, setEmploymentStatus] = useState<'PERMANENT' | 'SELF_EMPLOYED' | 'TEMPORARY' | 'UNEMPLOYED'>('PERMANENT')
  const [employmentTenureMonths, setEmploymentTenureMonths] = useState('60')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const [submitting, setSubmitting] = useState<string | null>(null)
  const load = useCallback(() => api.myApplications(token), [token])
  useEffect(() => { if (!token) return; let active = true; load().then((data) => { if (active) { setApplications(data); setLoadError('') } }).catch((reason: unknown) => { if (active) setLoadError(errorMessage(reason)) }); return () => { active = false } }, [load, reload])

  const create = async (event: React.FormEvent) => {
    event.preventDefault(); setBusy(true); setError('')
    try { await api.createApplication(token, { productType, requestedAmount: Number(amount), currency, termMonths: Number(termMonths), purpose, monthlyIncome: Number(monthlyIncome), existingMonthlyDebtObligations: Number(existingMonthlyDebtObligations), employmentStatus, employmentTenureMonths: Number(employmentTenureMonths) }); setReload((value) => value + 1) }
    catch (reason) { setError(errorMessage(reason)) }
    finally { setBusy(false) }
  }
  const submit = async (id: string) => {
    setSubmitting(id); setError('')
    try { await api.submitApplication(token, id); setReload((value) => value + 1) }
    catch (reason) { setError(errorMessage(reason)) }
    finally { setSubmitting(null) }
  }

  return <Shell><PageHeader eyebrow="PORTAL DEL CLIENTE" title="Mis solicitudes" description="Iniciá una solicitud y seguí su avance sin perder el historial." />
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Historial de solicitudes</h2><p>Las solicitudes pertenecen a tu cuenta autenticada.</p></div></div>{applications ? applications.length ? <div className={styles.list}>{applications.map((application) => <article className={styles.application} key={application.id}><div className={styles.icon}>⌂</div><div className={styles.main}><div className={styles.title}><h3>{application.productType.replaceAll('_', ' ')}</h3><StatusBadge value={application.status} /></div><p><Money value={application.requestedAmount} currency={application.currency} /> · <Term months={application.termMonths} /> · Actualizada {new Date(application.updatedAt).toLocaleDateString('es-AR')}</p></div><Link className={styles.applicationLink} to={`/applications/${application.id}`}>Detalle →</Link>{application.status === 'DRAFT' && <button className={styles.buttonSmall} disabled={busy || submitting !== null} onClick={() => void submit(application.id)}>{submitting === application.id ? 'Enviando…' : 'Enviar'}</button>}</article>)}</div> : <Empty title="Todavía no hay solicitudes" text="Completá el formulario para iniciar tu primera solicitud." /> : <LoadingError error={loadError} />}</section>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Nueva solicitud</h2><p>Ingresá tus datos financieros declarados para evaluar la capacidad de pago.</p></div></div><form className={styles.form} onSubmit={(event) => void create(event)}><label>Producto<select value={productType} disabled><option value="PERSONAL_LOAN">Préstamo personal</option></select></label><div className={styles.row}><label>Monto solicitado<input type="number" min="0.01" step="0.01" value={amount} onChange={(event) => setAmount(event.target.value)} required /></label><label>Moneda<select value={currency} onChange={(event) => setCurrency(event.target.value)}><option>ARS</option><option>USD</option></select></label></div><label>Plazo en meses<input type="number" min="1" max="60" value={termMonths} onChange={(event) => setTermMonths(event.target.value)} required /></label><div className={styles.row}><label>Ingreso mensual declarado<input type="number" min="0.01" step="0.01" value={monthlyIncome} onChange={(event) => setMonthlyIncome(event.target.value)} required /></label><label>Deudas mensuales existentes<input type="number" min="0" step="0.01" value={existingMonthlyDebtObligations} onChange={(event) => setExistingMonthlyDebtObligations(event.target.value)} required /></label></div><div className={styles.row}><label>Situación laboral<select value={employmentStatus} onChange={(event) => setEmploymentStatus(event.target.value as typeof employmentStatus)}><option value="PERMANENT">Relación de dependencia</option><option value="SELF_EMPLOYED">Independiente</option><option value="TEMPORARY">Temporal</option><option value="UNEMPLOYED">Sin empleo</option></select></label><label>Antigüedad laboral (meses)<input type="number" min="0" max="600" value={employmentTenureMonths} onChange={(event) => setEmploymentTenureMonths(event.target.value)} required /></label></div><label>Destino<textarea maxLength={500} value={purpose} onChange={(event) => setPurpose(event.target.value)} required /></label>{error && <Notice>{error}</Notice>}<button className={styles.button} disabled={busy}>{busy ? 'Creando…' : 'Crear solicitud'}</button></form></section>
  </Shell>
}
