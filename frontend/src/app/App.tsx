import type { ReactNode } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { CustomerBootstrap } from '../features/auth/CustomerBootstrap'
import { LoginPage } from '../features/auth/LoginPage'
import { ApplicationsPage } from '../features/applications/ApplicationsPage'
import { ApplicationQueue } from '../features/applications/ApplicationQueue'
import { ApplicationPage } from '../features/applications/ApplicationPage'
import { CustomerDashboard } from '../features/dashboard/CustomerDashboard'
import { OperationsDashboard } from '../features/dashboard/OperationsDashboard'
import { AuditorDashboard } from '../features/dashboard/AuditorDashboard'
import { LoansPage } from '../features/loans/LoansPage'
import { LoanPage } from '../features/loans/LoanPage'
import { OffersPage } from '../features/offers/OffersPage'
import { OfferPage } from '../features/offers/OfferPage'
import { AuditExplorer } from '../features/audit/AuditExplorer'
import { CustomerPage } from '../features/customers/CustomerPage'
import { rolesFromProfile, hasAnyRole, type AppRole } from '../shared/auth/roles'
import { Notice } from '../shared/components/Primitives'
import styles from './App.module.css'

function RoleGate({ allow, children }: { allow: AppRole[]; children: ReactNode }) {
  const roles = rolesFromProfile(useAuth().user?.profile)
  return hasAnyRole(roles, allow) ? <>{children}</> : <main className={styles.state}><section className={styles.unauthorized}><h1>Acceso no disponible</h1><Notice>Tu rol no tiene acceso a esta sección. Si creés que es un error, consultá al administrador.</Notice></section></main>
}

function Home() {
  const roles = rolesFromProfile(useAuth().user?.profile)
  if (roles.includes('CUSTOMER')) return <CustomerDashboard />
  if (roles.includes('LOAN_OFFICER')) return <OperationsDashboard />
  if (roles.includes('AUDITOR')) return <AuditorDashboard />
  if (roles.includes('ADMIN')) return <OperationsDashboard admin />
  return <main className={styles.state}><Notice>El proveedor de identidad no asignó un rol compatible a este usuario.</Notice></main>
}

export function App() {
  const auth = useAuth()
  if (auth.isLoading) return <main className={styles.state}>Conectando con el proveedor de identidad…</main>
  if (auth.error) return <main className={styles.state}><Notice>{auth.error.message}</Notice></main>
  if (!auth.isAuthenticated) return <Routes><Route path="/login" element={<LoginPage />} /><Route path="*" element={<Navigate to="/login" replace />} /></Routes>

  return <CustomerBootstrap><Routes>
    <Route path="/login" element={<Navigate to="/dashboard" replace />} />
    <Route path="/dashboard" element={<Home />} />
    <Route path="/applications" element={<RoleGate allow={['CUSTOMER', 'LOAN_OFFICER', 'ADMIN']}><ApplicationsIndex /></RoleGate>} />
    <Route path="/applications/:id" element={<RoleGate allow={['CUSTOMER', 'LOAN_OFFICER', 'AUDITOR', 'ADMIN']}><ApplicationPage /></RoleGate>} />
    <Route path="/offers" element={<RoleGate allow={['CUSTOMER']}><OffersPage /></RoleGate>} />
    <Route path="/offers/:id" element={<RoleGate allow={['CUSTOMER']}><OfferPage /></RoleGate>} />
    <Route path="/loans" element={<RoleGate allow={['CUSTOMER', 'LOAN_OFFICER', 'ADMIN']}><LoansPage /></RoleGate>} />
    <Route path="/loans/:id" element={<RoleGate allow={['CUSTOMER', 'LOAN_OFFICER', 'AUDITOR', 'ADMIN']}><LoanPage /></RoleGate>} />
    <Route path="/customers/:id" element={<RoleGate allow={['LOAN_OFFICER', 'ADMIN']}><CustomerPage /></RoleGate>} />
    <Route path="/audit" element={<RoleGate allow={['AUDITOR', 'ADMIN']}><AuditExplorer /></RoleGate>} />
    <Route path="*" element={<Navigate to="/dashboard" replace />} />
  </Routes></CustomerBootstrap>
}

function ApplicationsIndex() {
  const roles = rolesFromProfile(useAuth().user?.profile)
  return roles.includes('CUSTOMER') ? <ApplicationsPage /> : <ApplicationQueue />
}
