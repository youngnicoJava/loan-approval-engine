export type LoanApplication = {
  id: string
  customerId: string
  productType: string
  requestedAmount: number
  currency: string
  termMonths: number
  purpose: string
  monthlyIncome: number | null
  existingMonthlyDebtObligations: number | null
  employmentStatus: ApplicantEmploymentStatus | null
  employmentTenureMonths: number | null
  status: LoanApplicationStatus
  createdAt: string
  submittedAt: string | null
  updatedAt: string
}

export type Customer = {
  id: string
  externalIdentityId: string
  fullName: string
  email: string
  status: CustomerStatus
  createdAt: string
}

export type LoanOffer = {
  id: string
  loanApplicationId: string
  customerId: string
  principal: number
  currency: string
  termMonths: number
  annualInterestRatePercentage: number
  monthlyInstallment: number
  totalRepayment: number
  installmentFrequency: string
  status: LoanOfferStatus
  createdAt: string
  expiresAt: string
  acceptedAt: string | null
  declinedAt: string | null
}

export type Loan = {
  id: string
  loanOfferId: string
  loanApplicationId: string
  customerId: string
  principal: number
  currency: string
  termMonths: number
  annualInterestRatePercentage: number
  monthlyInstallment: number
  totalRepayment: number
  status: LoanStatus
  createdAt: string
  activatedAt: string | null
  paidOffAt: string | null
}

export type Disbursement = {
  id: string
  loanId: string
  amount: number
  currency: string
  status: DisbursementStatus
  requestedAt: string
  processingAt: string | null
  completedAt: string | null
  failedAt: string | null
  cancelledAt: string | null
}

export type RepaymentSchedule = {
  id: string
  loanId: string
  amortizationType: string
  principal: number
  currency: string
  annualInterestRatePercentage: number
  termMonths: number
  disbursedOn: string
  status: string
  createdAt: string
  installmentCount: number
}

export type Installment = {
  id: string
  loanId: string
  installmentNumber: number
  dueDate: string
  principalAmount: number
  interestAmount: number
  totalAmount: number
  remainingPrincipal: number
  currency: string
  status: InstallmentStatus
}

export type ApiError = {
  code: string
  message: string
  timestamp: string
  correlationId: string | null
}

export type CreateApplication = {
  productType: string
  requestedAmount: number
  currency: string
  termMonths: number
  purpose: string
  monthlyIncome: number
  existingMonthlyDebtObligations: number
  employmentStatus: ApplicantEmploymentStatus
  employmentTenureMonths: number
}

export type ApplicantEmploymentStatus = 'PERMANENT' | 'SELF_EMPLOYED' | 'TEMPORARY' | 'UNEMPLOYED'

export type RiskAssessment = {
  id: string
  loanApplicationId: string
  decision: 'APPROVE' | 'REJECT' | 'REFER'
  reasonCode: string
  source: string
  assessedAt: string
}

export type ExternalRiskRequestResponse = {
  loanApplicationId: string
  status: string
  message: string
}

export type EvaluateRiskResponse = RiskAssessment | ExternalRiskRequestResponse

export type AuditEvent = {
  id: string
  eventId: string
  actorId: string
  actorType: string
  action: string
  aggregateType: string
  aggregateId: string
  occurredAt: string
  correlationId: string
  metadata: string
}

export type IssueOffer = {
  offeredPrincipal: number
  offeredTermMonths: number
  annualInterestRatePercentage: number
  expiresAt: string
}

export type CustomerStatus = 'ACTIVE' | 'BLOCKED'
export type LoanApplicationStatus = 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' | 'CANCELLED'
export type LoanOfferStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED'
export type LoanStatus = 'PENDING_DISBURSEMENT' | 'ACTIVE' | 'PAID_OFF' | 'DEFAULTED' | 'CANCELLED'
export type DisbursementStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED'
export type InstallmentStatus = 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED'
