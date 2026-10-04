import styles from './ConfirmDialog.module.css'

type Props = { open: boolean; title: string; description: string; confirmLabel: string; danger?: boolean; busy?: boolean; onConfirm: () => void; onCancel: () => void }
export function ConfirmDialog({ open, title, description, confirmLabel, danger = false, busy = false, onConfirm, onCancel }: Props) {
  if (!open) return null
  return <div className={styles.backdrop} role="presentation"><section className={styles.dialog} role="dialog" aria-modal="true" aria-labelledby="confirmation-title"><div className={styles.eyebrow}>CONFIRMAR OPERACIÓN</div><h2 id="confirmation-title">{title}</h2><p>{description}</p><div className={styles.actions}><button className={styles.secondary} disabled={busy} onClick={onCancel}>Cancelar</button><button className={danger ? styles.danger : styles.primary} disabled={busy} onClick={onConfirm}>{busy ? 'Procesando…' : confirmLabel}</button></div></section></div>
}
