export type LoanApplication = {
  id: string
  customerId: string
  productType: string
  requestedAmount: number
  currency: string
  termMonths: number
  purpose: string
  status: string
  createdAt: string
  submittedAt: string | null
  updatedAt: string
}

export type Customer = {
  id: string
  externalIdentityId: string
  fullName: string
  email: string
  status: string
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
  status: string
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
  status: string
  createdAt: string
  activatedAt: string | null
  paidOffAt: string | null
}

export type Disbursement = {
  id: string
  loanId: string
  amount: number
  currency: string
  status: string
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
  status: string
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
}
