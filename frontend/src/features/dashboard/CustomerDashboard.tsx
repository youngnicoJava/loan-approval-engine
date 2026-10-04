import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api } from '../../shared/api/client'
import type { Customer, Loan, LoanApplication, LoanOffer } from '../../shared/types/api'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { Money, PageHeader, Rate, Term } from '../../shared/components/Financial'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import styles from './DashboardPage.module.css'

function useCustomerData<T>(load: (token: string) => Promise<T>) {
  const token = useAuth().user?.access_token
  const [value, setValue] = useState<T | null>(null)
  const [error, setError] = useState('')
  useEffect(() => {
    if (!token) return
    let active = true
    load(token).then((data) => { if (active) { setValue(data); setError('') } }).catch((reason: unknown) => { if (active) setError(reason instanceof Error ? reason.message : 'No se pudo cargar la información.') })
    return () => { active = false }
  }, [token, load])
  return { value, error }
}

export function CustomerDashboard() {
  const loadCustomer = useCallback((token: string) => api.currentCustomer(token), [])
  const loadLoans = useCallback((token: string) => api.loans(token), [])
  const loadOffers = useCallback((token: string) => api.offers(token), [])
  const loadApplications = useCallback((token: string) => api.myApplications(token), [])
  const customerData = useCustomerData(loadCustomer)
  const loanData = useCustomerData(loadLoans)
  const offerData = useCustomerData(loadOffers)
  const applicationData = useCustomerData(loadApplications)
  const loans = loanData.value as Loan[] | null
  const offers = offerData.value as LoanOffer[] | null
  const applications = applicationData.value as LoanApplication[] | null
  const customer = customerData.value as Customer | null
  const navigate = useNavigate()
  const pendingOffer = offers?.find((offer) => offer.status === 'PENDING')

  return <Shell><PageHeader eyebrow="PORTAL DEL CLIENTE" title={customer?.fullName ? `Hola, ${customer.fullName.split(' ')[0]}` : 'Tu espacio financiero'} description="Tus solicitudes, ofertas y préstamos en un mismo lugar." action={<button className={styles.button} onClick={() => navigate('/applications')}>＋ Nueva solicitud</button>} />
    <div className={styles.summary}><Summary label="Solicitudes" value={applications?.length ?? '—'} detail="Tus solicitudes" /><Summary label="Ofertas pendientes" value={offers?.filter((offer) => offer.status === 'PENDING').length ?? '—'} detail="Listas para revisar" /><Summary label="Préstamos activos" value={loans?.filter((loan) => loan.status === 'ACTIVE').length ?? '—'} detail="En tu cuenta" /></div>
    {pendingOffer && <section className={styles.nextAction}><div><span className={styles.nextLabel}>PRÓXIMO PASO</span><h2>Tenés una oferta para revisar</h2><p><Money value={pendingOffer.principal} currency={pendingOffer.currency} /> · <Term months={pendingOffer.termMonths} /> · Cuota <Money value={pendingOffer.monthlyInstallment} currency={pendingOffer.currency} /></p></div><Link className={styles.nextButton} to={`/offers/${pendingOffer.id}`}>Ver oferta →</Link></section>}
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Mis solicitudes</h2><p>Estado de cada solicitud que iniciaste</p></div><Link className={styles.link} to="/applications">Ver todas →</Link></div>{applications ? applications.length ? <div className={styles.list}>{applications.map((application) => <article key={application.id} className={styles.item}><div className={styles.icon}>⌂</div><div className={styles.main}><div className={styles.title}><h3>{application.productType.replaceAll('_', ' ')}</h3><StatusBadge value={application.status} /></div><p><Money value={application.requestedAmount} currency={application.currency} /> · <Term months={application.termMonths} /> · Actualizada {new Date(application.updatedAt).toLocaleDateString('es-AR')}</p></div><Link className={styles.link} to={`/applications/${application.id}`}>Detalle →</Link></article>)}</div> : <Empty title="Todavía no hay solicitudes" text="Comenzá una solicitud para seguir su progreso desde aquí." /> : <LoadingError error={applicationData.error} />}</section>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Mis ofertas</h2><p>Importes y cuotas calculados por el backend</p></div><Link className={styles.link} to="/offers">Ver todas →</Link></div>{offers ? offers.length ? <div className={styles.list}>{offers.map((offer) => <article key={offer.id} className={styles.item}><div className={`${styles.icon} ${styles.offerIcon}`}>✦</div><div className={styles.main}><div className={styles.title}><h3><Money value={offer.principal} currency={offer.currency} /></h3><StatusBadge value={offer.status} /></div><p><Term months={offer.termMonths} /> · <Rate value={offer.annualInterestRatePercentage} /> · Cuota <Money value={offer.monthlyInstallment} currency={offer.currency} /></p></div>{offer.status === 'PENDING' && <Link className={styles.link} to={`/offers/${offer.id}`}>Revisar →</Link>}</article>)}</div> : <Empty title="No hay ofertas todavía" text="Cuando una solicitud sea aprobada, la oferta aparecerá aquí." /> : <LoadingError error={offerData.error} />}</section>
    <section className={styles.section}><div className={styles.sectionHeading}><div><h2>Mis préstamos</h2><p>Consultá el estado y el plan de pagos</p></div><Link className={styles.link} to="/loans">Ver todos →</Link></div>{loans ? loans.length ? <div className={styles.list}>{loans.map((loan) => <article key={loan.id} className={styles.item}><div className={styles.icon}>↗</div><div className={styles.main}><div className={styles.title}><h3><Money value={loan.principal} currency={loan.currency} /></h3><StatusBadge value={loan.status} /></div><p><Term months={loan.termMonths} /> · <Rate value={loan.annualInterestRatePercentage} /> · Cuota <Money value={loan.monthlyInstallment} currency={loan.currency} /></p></div><Link className={styles.link} to={`/loans/${loan.id}`}>Ver plan →</Link></article>)}</div> : <Empty title="Todavía no tenés préstamos" text="Al aceptar una oferta, el préstamo y su calendario se mostrarán aquí." /> : <LoadingError error={loanData.error} />}</section>
  </Shell>
}

function Summary({ label, value, detail }: { label: string; value: number | string; detail: string }) { return <div className={styles.summaryCard}><span>{label}</span><strong>{value}</strong><small>{detail}</small></div> }
