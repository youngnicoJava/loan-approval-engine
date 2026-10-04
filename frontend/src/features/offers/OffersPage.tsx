import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { LoanOffer } from '../../shared/types/api'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { Money, PageHeader, Rate, Term } from '../../shared/components/Financial'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import styles from './OffersPage.module.css'

export function OffersPage() {
  const token = useAuth().user?.access_token ?? ''
  const [offers, setOffers] = useState<LoanOffer[] | null>(null)
  const [error, setError] = useState('')
  useEffect(() => { if (!token) return; let active = true; api.offers(token).then(value => { if (active) setOffers(value) }).catch(reason => { if (active) setError(errorMessage(reason)) }); return () => { active = false } }, [token])
  return <Shell><PageHeader eyebrow="PORTAL DEL CLIENTE" title="Mis ofertas" description="Revisá los términos calculados para tus solicitudes." />{offers ? offers.length ? <div className={styles.list}>{offers.map(offer => <article className={styles.card} key={offer.id}><div className={styles.heading}><div><small>CAPITAL OFRECIDO</small><h2><Money value={offer.principal} currency={offer.currency} /></h2></div><StatusBadge value={offer.status} /></div><div className={styles.terms}><div><span>Tasa</span><strong><Rate value={offer.annualInterestRatePercentage} /></strong></div><div><span>Plazo</span><strong><Term months={offer.termMonths} /></strong></div><div><span>Cuota mensual</span><strong><Money value={offer.monthlyInstallment} currency={offer.currency} /></strong></div><div><span>Total a devolver</span><strong><Money value={offer.totalRepayment} currency={offer.currency} /></strong></div><div><span>Vence</span><strong>{new Date(offer.expiresAt).toLocaleString('es-AR')}</strong></div></div><Link className={styles.link} to={`/offers/${offer.id}`}>Inspeccionar oferta →</Link></article>)}</div> : <Empty title="No hay ofertas" text="Las ofertas emitidas para tus solicitudes aparecerán aquí." /> : <LoadingError error={error} />}</Shell>
}
