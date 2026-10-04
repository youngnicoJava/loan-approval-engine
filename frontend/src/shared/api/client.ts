import type {
  ApiError, AuditEvent, CreateApplication, Customer, Disbursement, EvaluateRiskResponse, Installment,
  IssueOffer, Loan, LoanApplication, LoanOffer, LoanStatus, RepaymentSchedule,
  RiskAssessment,
} from '../types/api'

export type CreateCustomer = Pick<Customer, 'fullName' | 'email'>

export class ApiRequestError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly code: string | null,
    readonly correlationId: string | null,
    readonly backendMessage: string | null,
  ) {
    super(message)
    this.name = 'ApiRequestError'
  }
}

const baseUrl = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '')
const requestId = () => crypto.randomUUID()

async function request<T>(path: string, token: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, {
    ...init,
    headers: {
      Accept: 'application/json',
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      'X-Correlation-ID': requestId(),
      ...(init.headers ?? {}),
    },
  })
  if (!response.ok) {
    const problem = await response.json().catch(() => null) as ApiError | null
    throw new ApiRequestError(
      problem?.message ?? `La solicitud falló (${response.status}).`,
      response.status,
      problem?.code ?? null,
      problem?.correlationId ?? response.headers.get('X-Correlation-ID'),
      problem?.message ?? null,
    )
  }
  if (response.status === 204) return null as T
  return response.json() as Promise<T>
}

const idempotent = () => ({ 'Idempotency-Key': requestId() })

export const api = {
  currentCustomer: (token: string) => request<Customer>('/api/v1/customers/me', token),
  customer: (token: string, id: string) => request<Customer>(`/api/v1/customers/${id}`, token),
  createCustomer: (token: string, body: CreateCustomer) => request<Customer>('/api/v1/customers', token, { method: 'POST', body: JSON.stringify(body) }),
  blockCustomer: (token: string, id: string) => request<Customer>(`/api/v1/customers/${id}/block`, token, { method: 'POST' }),
  activateCustomer: (token: string, id: string) => request<Customer>(`/api/v1/customers/${id}/activate`, token, { method: 'POST' }),

  applications: (token: string, id: string) => request<LoanApplication>(`/api/v1/loan-applications/${id}`, token),
  myApplications: (token: string) => request<LoanApplication[]>('/api/v1/loan-applications/mine', token),
  applicationQueue: (token: string, status: string | null, offset = 0, limit = 50) => {
    const query = new URLSearchParams({ offset: String(offset), limit: String(limit) })
    if (status) query.set('status', status)
    return request<LoanApplication[]>(`/api/v1/loan-applications?${query}`, token)
  },
  createApplication: (token: string, body: CreateApplication) => request<LoanApplication>('/api/v1/loan-applications', token, { method: 'POST', headers: idempotent(), body: JSON.stringify(body) }),
  submitApplication: (token: string, id: string) => request<LoanApplication>(`/api/v1/loan-applications/${id}/submit`, token, { method: 'POST', headers: idempotent() }),
  evaluateApplication: (token: string, id: string) => request<EvaluateRiskResponse>(`/api/v1/loan-applications/${id}/evaluate`, token, { method: 'POST' }),
  riskAssessment: (token: string, id: string) => request<RiskAssessment | null>(`/api/v1/loan-applications/${id}/risk-assessment`, token),
  approveApplication: (token: string, id: string) => request<LoanApplication>(`/api/v1/loan-applications/${id}/approve`, token, { method: 'POST', headers: idempotent() }),
  rejectApplication: (token: string, id: string) => request<LoanApplication>(`/api/v1/loan-applications/${id}/reject`, token, { method: 'POST', headers: idempotent() }),

  offers: (token: string) => request<LoanOffer[]>('/api/v1/loan-offers/mine', token),
  offer: (token: string, id: string) => request<LoanOffer>(`/api/v1/loan-offers/${id}`, token),
  offerForApplication: (token: string, id: string) => request<LoanOffer>(`/api/v1/loan-offers/by-application/${id}`, token),
  issueOffer: (token: string, applicationId: string, body: IssueOffer) => request<LoanOffer>(`/api/v1/loan-offers/from-application/${applicationId}`, token, { method: 'POST', body: JSON.stringify(body) }),
  declineOffer: (token: string, id: string) => request<LoanOffer>(`/api/v1/loan-offers/${id}/decline`, token, { method: 'POST' }),
  acceptOffer: (token: string, id: string) => request<Loan>(`/api/v1/loan-offers/${id}/accept`, token, { method: 'POST', headers: idempotent() }),

  loans: (token: string) => request<Loan[]>('/api/v1/loans/mine', token),
  operationalLoans: (token: string, status: LoanStatus | '') => {
    const query = status ? `?status=${encodeURIComponent(status)}&limit=100` : '?limit=100'
    return request<Loan[]>(`/api/v1/loans${query}`, token)
  },
  loan: (token: string, id: string) => request<Loan>(`/api/v1/loans/${id}`, token),
  disburse: (token: string, id: string) => request<Disbursement>(`/api/v1/loans/${id}/disbursement`, token, { method: 'POST', headers: idempotent() }),
  disbursement: (token: string, id: string) => request<Disbursement>(`/api/v1/loans/${id}/disbursement`, token),
  schedule: (token: string, id: string) => request<RepaymentSchedule>(`/api/v1/loans/${id}/repayment-schedule`, token),
  installments: (token: string, id: string) => request<Installment[]>(`/api/v1/loans/${id}/installments`, token),

  audit: (token: string, offset = 0, limit = 50) => request<AuditEvent[]>(`/api/v1/audit/events?offset=${offset}&limit=${limit}`, token),
  auditForAggregate: (token: string, id: string, offset = 0, limit = 50) => request<AuditEvent[]>(`/api/v1/audit/events/${id}?offset=${offset}&limit=${limit}`, token),
}

export function errorMessage(error: unknown): string {
  if (error instanceof ApiRequestError) {
    const code = error.code ? ` [${error.code}]` : ` [HTTP ${error.status}]`
    const correlation = error.correlationId ? ` · Correlation ID: ${error.correlationId}` : ''
    return `${error.message}${code}${correlation}`
  }
  return error instanceof Error ? error.message : 'Ocurrió un error inesperado.'
}
