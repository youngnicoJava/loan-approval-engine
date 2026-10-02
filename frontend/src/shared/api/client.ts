import type { ApiError, CreateApplication, Customer, Disbursement, Installment, Loan, LoanApplication, LoanOffer, RepaymentSchedule } from '../types/api'

const baseUrl = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '')

function correlationId(): string {
  return crypto.randomUUID()
}

async function request<T>(path: string, token: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, {
    ...init,
    headers: {
      Accept: 'application/json',
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      'X-Correlation-ID': correlationId(),
      ...(init.headers ?? {}),
    },
  })
  if (!response.ok) {
    const problem = await response.json().catch(() => null) as ApiError | null
    throw new Error(problem?.message ?? `Request failed (${response.status})`)
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

export const api = {
  currentCustomer: (token: string) => request<Customer>('/api/v1/customers/me', token),
  applications: (token: string, id: string) => request<LoanApplication>(`/api/v1/loan-applications/${id}`, token),
  createApplication: (token: string, body: CreateApplication) => request<LoanApplication>('/api/v1/loan-applications', token, { method: 'POST', headers: { 'Idempotency-Key': crypto.randomUUID() }, body: JSON.stringify(body) }),
  submitApplication: (token: string, id: string) => request<LoanApplication>(`/api/v1/loan-applications/${id}/submit`, token, { method: 'POST', headers: { 'Idempotency-Key': crypto.randomUUID() } }),
  offers: (token: string) => request<LoanOffer[]>('/api/v1/loan-offers/mine', token),
  offer: (token: string, id: string) => request<LoanOffer>(`/api/v1/loan-offers/${id}`, token),
  acceptOffer: (token: string, id: string) => request<Loan>(`/api/v1/loan-offers/${id}/accept`, token, { method: 'POST', headers: { 'Idempotency-Key': crypto.randomUUID() } }),
  declineOffer: (token: string, id: string) => request<LoanOffer>(`/api/v1/loan-offers/${id}/decline`, token, { method: 'POST' }),
  loans: (token: string) => request<Loan[]>('/api/v1/loans/mine', token),
  loan: (token: string, id: string) => request<Loan>(`/api/v1/loans/${id}`, token),
  disbursement: (token: string, id: string) => request<Disbursement>(`/api/v1/loans/${id}/disbursement`, token),
  schedule: (token: string, id: string) => request<RepaymentSchedule>(`/api/v1/loans/${id}/repayment-schedule`, token),
  installments: (token: string, id: string) => request<Installment[]>(`/api/v1/loans/${id}/installments`, token),
}

export function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : 'Ocurrió un error inesperado.'
}
