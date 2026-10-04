import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { api, errorMessage } from '../../shared/api/client'
import type { AuditEvent } from '../../shared/types/api'
import { DateTime, PageHeader } from '../../shared/components/Financial'
import { Empty, LoadingError } from '../../shared/components/Primitives'
import { Shell } from '../../shared/components/Shell'
import styles from './AuditExplorer.module.css'

export function AuditExplorer() {
  const token=useAuth().user?.access_token??''; const [params,setParams]=useSearchParams(); const aggregateId=params.get('aggregateId')??''; const [value,setValue]=useState(aggregateId); const [events,setEvents]=useState<AuditEvent[]|null>(null); const [error,setError]=useState(''); const [offset,setOffset]=useState(0); const limit=50
  useEffect(()=>{if(!token)return;let active=true;setEvents(null);setError('');const load=aggregateId?api.auditForAggregate(token,aggregateId,offset,limit):api.audit(token,offset,limit);load.then(items=>{if(active)setEvents(items)}).catch(reason=>{if(active)setError(errorMessage(reason))});return()=>{active=false}},[token,aggregateId,offset])
  const copy=(text:string)=>{void navigator.clipboard?.writeText(text)}
  return <Shell role="AUDITOR"><PageHeader eyebrow="SÓLO LECTURA" title="Explorador de auditoría" description="Eventos funcionales append-only con identidad, agregado y correlation ID."/><form className={styles.search} onSubmit={event=>{event.preventDefault();setOffset(0);setParams(value.trim()?{aggregateId:value.trim()}: {})}}><label>Filtrar por ID de agregado<input value={value} onChange={event=>setValue(event.target.value)} placeholder="UUID del agregado"/></label><button>Buscar</button>{aggregateId&&<button type="button" className={styles.secondary} onClick={()=>{setValue('');setParams({});setOffset(0)}}>Ver todos</button>}</form>{events?events.length?<div className={styles.list}>{events.map(event=><article className={styles.event} key={event.id}><div className={styles.head}><div><h2>{event.action.replaceAll('_',' ')}</h2><span>{event.aggregateType}</span></div><time><DateTime value={event.occurredAt}/></time></div><div className={styles.fields}><Field label="Actor" value={`${event.actorType} · ${event.actorId}`} copy={copy}/><Field label="Aggregate ID" value={event.aggregateId} copy={copy}/><Field label="Correlation ID" value={event.correlationId||'—'} copy={copy}/><Field label="Event ID" value={event.eventId} copy={copy}/></div>{event.metadata&&<details><summary>Metadata</summary><pre>{formatMetadata(event.metadata)}</pre></details>}</article>)}</div>:<Empty title="No hay eventos" text={aggregateId?'No hay eventos para este agregado.':'Todavía no se registraron eventos de auditoría.'}/>:<LoadingError error={error||'Cargando auditoría…'}/>}<div className={styles.pagination}><button disabled={offset===0} onClick={()=>setOffset(Math.max(0,offset-limit))}>← Anterior</button><span>{events?.length?`${offset+1}–${offset+events.length}`:'0 eventos'}</span><button disabled={!events||events.length<limit} onClick={()=>setOffset(offset+limit)}>Siguiente →</button></div></Shell>
}
function Field({label,value,copy}:{label:string;value:string;copy:(text:string)=>void}){return <div className={styles.field}><span>{label}</span><div><code>{value}</code>{value!=='—'&&<button title={`Copiar ${label}`} onClick={()=>copy(value)}>Copiar</button>}</div></div>}
function formatMetadata(value:string){try{return JSON.stringify(JSON.parse(value),null,2)}catch{return value}}
