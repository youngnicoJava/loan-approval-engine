import styles from './StatusBadge.module.css'

const labels: Record<string, string> = {
  DRAFT: 'Borrador', SUBMITTED: 'Enviada', UNDER_REVIEW: 'En revisión', APPROVED: 'Aprobada',
  REJECTED: 'Rechazada', CANCELLED: 'Cancelada', APPROVE: 'Aprobar', REJECT: 'Rechazar',
  REFER: 'Revisión manual', PENDING: 'Pendiente', ACCEPTED: 'Aceptada', DECLINED: 'Rechazada',
  EXPIRED: 'Vencida', PENDING_DISBURSEMENT: 'Pendiente de desembolso', ACTIVE: 'Activa',
  PAID_OFF: 'Cancelado', DEFAULTED: 'En mora', PROCESSING: 'Procesando', COMPLETED: 'Completado',
  FAILED: 'Fallido', PAID: 'Pagada', OVERDUE: 'Vencida', BLOCKED: 'Bloqueado',
}

const tones: Record<string, string> = {
  APPROVED: 'success', APPROVE: 'success', ACTIVE: 'success', ACCEPTED: 'success', COMPLETED: 'success', PAID: 'success',
  PENDING: 'warning', DRAFT: 'muted', SUBMITTED: 'info', UNDER_REVIEW: 'info', REFER: 'warning',
  PENDING_DISBURSEMENT: 'warning', PROCESSING: 'info', EXPIRED: 'muted', CANCELLED: 'muted', DECLINED: 'muted',
  REJECTED: 'danger', REJECT: 'danger', FAILED: 'danger', OVERDUE: 'danger', DEFAULTED: 'danger', BLOCKED: 'danger',
}

export function StatusBadge({ value }: { value: string }) {
  return <span className={`${styles.badge} ${styles[tones[value] ?? 'muted']}`} title={value}>{labels[value] ?? value.replaceAll('_', ' ')}</span>
}
