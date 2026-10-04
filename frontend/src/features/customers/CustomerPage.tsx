import { useEffect, useState } from 'react'
import { useAuth } from 'react-oidc-context'
import { useParams } from 'react-router-dom'
import { api, errorMessage } from '../../shared/api/client'
import type { Customer } from '../../shared/types/api'
import { ConfirmDialog } from '../../shared/components/ConfirmDialog'
import { DateTime, FinancialGrid, FinancialValue, PageHeader } from '../../shared/components/Financial'
import { LoadingError, Notice } from '../../shared/components/Primitives'
import { StatusBadge } from '../../shared/components/StatusBadge'
import { Shell } from '../../shared/components/Shell'
import styles from './CustomerPage.module.css'

export function CustomerPage() {
  const { id = '' } = useParams(); const token = useAuth().user?.access_token ?? ''; const [customer,setCustomer]=useState<Customer|null>(null); const [error,setError]=useState(''); const [busy,setBusy]=useState(false); const [confirm,setConfirm]=useState(false); const [message,setMessage]=useState('')
  useEffect(()=>{if(!token)return;let active=true;api.customer(token,id).then(value=>{if(active)setCustomer(value)}).catch(reason=>{if(active)setError(errorMessage(reason))});return()=>{active=false}},[token,id])
  const mutate=async()=>{if(!customer)return;setBusy(true);setError('');setMessage('');try{const updated=customer.status==='ACTIVE'?await api.blockCustomer(token,id):await api.activateCustomer(token,id);setCustomer(updated);setMessage(updated.status==='BLOCKED'?'Cliente bloqueado.':'Cliente activado.')}catch(reason){setError(errorMessage(reason))}finally{setBusy(false);setConfirm(false)}}
  return <Shell><PageHeader eyebrow="GESTIÓN DE CLIENTES" title="Perfil de cliente" description="Información del cliente asociada por el backend." action={customer?<StatusBadge value={customer.status}/>:undefined}/>{customer?<section className={styles.card}><FinancialGrid><FinancialValue label="Nombre">{customer.fullName}</FinancialValue><FinancialValue label="Correo">{customer.email}</FinancialValue><FinancialValue label="Estado"><StatusBadge value={customer.status}/></FinancialValue><FinancialValue label="Alta"><DateTime value={customer.createdAt}/></FinancialValue></FinancialGrid><p className={styles.policy}>El cambio de estado afecta las reglas de negocio existentes para este cliente.</p>{error&&<Notice>{error}</Notice>}{message&&<Notice>{message}</Notice>}<button className={customer.status==='ACTIVE'?styles.danger:styles.primary} disabled={busy} onClick={()=>setConfirm(true)}>{customer.status==='ACTIVE'?'Bloquear cliente':'Activar cliente'}</button></section>:<LoadingError error={error||'Cargando perfil…'}/>}<ConfirmDialog open={confirm} title={customer?.status==='ACTIVE'?'Bloquear cliente':'Activar cliente'} description={customer?.status==='ACTIVE'?'El cliente quedará BLOCKED y se aplicarán las restricciones definidas por el backend.':'El cliente volverá a ACTIVE según el flujo administrativo existente.'} confirmLabel={customer?.status==='ACTIVE'?'Bloquear':'Activar'} danger={customer?.status==='ACTIVE'} busy={busy} onCancel={()=>setConfirm(false)} onConfirm={()=>void mutate()}/></Shell>
}
