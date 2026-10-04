import { useEffect, useState } from 'react'
import { useAuth } from 'react-oidc-context'
import { useNavigate, useParams } from 'react-router-dom'
import { api, errorMessage } from '../../shared/api/client'
import type { LoanOffer } from '../../shared/types/api'
import { Detail, LoadingError, money, Notice, statusName } from '../../shared/components/Primitives'
import { Shell } from '../../shared/components/Shell'
import { ConfirmDialog } from '../../shared/components/ConfirmDialog'
import styles from './OfferPage.module.css'

export function OfferPage() {
  const { id = '' } = useParams()
  const token = useAuth().user?.access_token ?? ''
  const [offer, setOffer] = useState<LoanOffer | null>(null)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [busy, setBusy] = useState(false)
  const [confirm, setConfirm] = useState<'accept' | 'decline' | null>(null)
  const [reload, setReload] = useState(0)
  const navigate = useNavigate()
  useEffect(() => { if (!token) return; let active = true; api.offer(token, id).then((data) => { if (active) setOffer(data) }).catch((reason: unknown) => { if (active) setError(errorMessage(reason)) }); return () => { active = false } }, [token, id, reload])
  const decide = async () => { if (!confirm) return; const accept = confirm === 'accept'; setBusy(true); setMessage(''); try { if (accept) { const loan = await api.acceptOffer(token, id); navigate(`/loans/${loan.id}`) } else { await api.declineOffer(token, id); setMessage('Oferta rechazada.'); setReload((value) => value + 1) } } catch (reason) { setMessage(errorMessage(reason)) } finally { setBusy(false); setConfirm(null) } }
  return <Shell><header className={styles.header}><div><div className={styles.eyebrow}>OFERTA DE PRÉSTAMO</div><h1>Revisá tu oferta</h1><p>Revisá los importes y las condiciones de pago.</p></div></header>{offer ? <section className={styles.card}><div className={styles.amount}>{money(offer.principal, offer.currency)}<span>Capital ofrecido</span></div><div className={styles.grid}><Detail label="Tasa nominal anual" value={`${offer.annualInterestRatePercentage}%`} /><Detail label="Plazo" value={`${offer.termMonths} meses`} /><Detail label="Cuota mensual" value={money(offer.monthlyInstallment, offer.currency)} /><Detail label="Total a devolver" value={money(offer.totalRepayment, offer.currency)} /><Detail label="Vence" value={new Date(offer.expiresAt).toLocaleDateString('es-AR')} /><Detail label="Estado" value={statusName(offer.status)} /></div>{message && <Notice>{message}</Notice>}{offer.status === 'PENDING' && <div className={styles.actions}><button className={styles.button} disabled={busy} onClick={() => setConfirm('accept')}>Aceptar oferta</button><button className={styles.secondary} disabled={busy} onClick={() => setConfirm('decline')}>Rechazar</button></div>}</section> : <LoadingError error={error} />}<ConfirmDialog open={confirm!==null} title={confirm==='accept'?'Aceptar oferta':'Rechazar oferta'} description={confirm==='accept'?'Al aceptar se creará el préstamo con estos términos.':'La oferta quedará DECLINED y no podrá aceptarse después.'} confirmLabel={confirm==='accept'?'Aceptar oferta':'Rechazar oferta'} danger={confirm==='decline'} busy={busy} onCancel={()=>setConfirm(null)} onConfirm={()=>void decide()}/></Shell>
}
