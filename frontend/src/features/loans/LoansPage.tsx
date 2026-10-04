import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { Loan, LoanStatus } from '../../shared/types/api'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { Money, PageHeader, Rate, Term } from '../../shared/components/Financial'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import { rolesFromProfile } from '../../shared/auth/roles'
import styles from './LoansPage.module.css'

const statuses: LoanStatus[] = ['PENDING_DISBURSEMENT', 'ACTIVE', 'PAID_OFF', 'DEFAULTED', 'CANCELLED']
export function LoansPage() {
  const auth = useAuth(); const token = auth.user?.access_token ?? ''; const roles = rolesFromProfile(auth.user?.profile); const operator = roles.includes('LOAN_OFFICER') || roles.includes('ADMIN')
  const [params, setParams] = useSearchParams(); const status = (params.get('status') ?? '') as LoanStatus | ''
  const [loans, setLoans] = useState<Loan[] | null>(null); const [error, setError] = useState('')
  useEffect(() => { if (!token) return; let active = true; setLoans(null); const load = operator ? api.operationalLoans(token, status) : api.loans(token); load.then(value => { if (active) loansSet(value) }).catch(reason => { if (active) setError(errorMessage(reason)) }); function loansSet(value: Loan[]) { setLoans(value) }; return () => { active = false } }, [token, operator, status])
  return <Shell role={roles.includes('ADMIN') ? 'ADMIN' : operator ? 'LOAN_OFFICER' : 'CUSTOMER'}><PageHeader eyebrow={operator ? 'OPERACIONES' : 'PORTAL DEL CLIENTE'} title={operator ? 'Préstamos' : 'Mis préstamos'} description={operator ? 'Consultá préstamos por estado y ejecutá desembolsos pendientes.' : 'Estado, desembolso y calendario de tus préstamos.'} action={operator ? <label className={styles.filter}>Estado<select value={status} onChange={event => setParams(event.target.value ? { status: event.target.value } : {})}><option value="">Todos</option>{statuses.map(value => <option key={value} value={value}>{value.replaceAll('_', ' ')}</option>)}</select></label> : undefined} />{loans ? loans.length ? <div className={styles.list}>{loans.map(loan => <article className={styles.card} key={loan.id}><div className={styles.info}><h2><Money value={loan.principal} currency={loan.currency} /></h2><p><Term months={loan.termMonths} /> · <Rate value={loan.annualInterestRatePercentage} /> · Cuota <Money value={loan.monthlyInstallment} currency={loan.currency} /></p></div><StatusBadge value={loan.status} /><Link className={styles.link} to={`/loans/${loan.id}`}>{loan.status === 'PENDING_DISBURSEMENT' && operator ? 'Gestionar desembolso →' : 'Ver préstamo →'}</Link></article>)}</div> : <Empty title="No hay préstamos para mostrar" text={operator ? 'No se encontraron préstamos con ese estado.' : 'Cuando aceptes una oferta, el préstamo aparecerá aquí.'} /> : <LoadingError error={error} />}</Shell>
}
