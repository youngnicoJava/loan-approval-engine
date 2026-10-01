CREATE TABLE disbursements
(
    id                 UUID PRIMARY KEY,
    loan_id            UUID          NOT NULL UNIQUE REFERENCES loans (id),
    amount             NUMERIC(19, 2) NOT NULL,
    currency           VARCHAR(3)    NOT NULL,
    status             VARCHAR(20)   NOT NULL,
    requested_at       TIMESTAMPTZ   NOT NULL,
    processing_at      TIMESTAMPTZ,
    completed_at       TIMESTAMPTZ,
    failed_at          TIMESTAMPTZ,
    cancelled_at       TIMESTAMPTZ,
    external_reference VARCHAR(255),
    failure_reason     VARCHAR(1000),
    CONSTRAINT ck_disbursements_amount CHECK (amount > 0),
    CONSTRAINT ck_disbursements_currency CHECK (length(currency) = 3 AND currency = upper(currency)),
    CONSTRAINT ck_disbursements_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'))
);

CREATE TABLE repayment_schedules
(
    id                              UUID PRIMARY KEY,
    loan_id                         UUID          NOT NULL REFERENCES loans (id),
    amortization_type               VARCHAR(40)   NOT NULL,
    principal                       NUMERIC(19, 2) NOT NULL,
    currency                        VARCHAR(3)    NOT NULL,
    annual_interest_rate_percentage NUMERIC(9, 4) NOT NULL,
    term_months                     INTEGER       NOT NULL,
    disbursed_on                    DATE          NOT NULL,
    status                          VARCHAR(20)   NOT NULL,
    created_at                      TIMESTAMPTZ   NOT NULL,
    CONSTRAINT uk_repayment_schedules_id_loan UNIQUE (id, loan_id),
    CONSTRAINT ck_repayment_schedules_type CHECK (amortization_type IN ('FRENCH_FIXED_PAYMENT')),
    CONSTRAINT ck_repayment_schedules_principal CHECK (principal > 0),
    CONSTRAINT ck_repayment_schedules_rate CHECK (annual_interest_rate_percentage >= 0),
    CONSTRAINT ck_repayment_schedules_term CHECK (term_months > 0),
    CONSTRAINT ck_repayment_schedules_currency CHECK (length(currency) = 3 AND currency = upper(currency)),
    CONSTRAINT ck_repayment_schedules_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'))
);
CREATE UNIQUE INDEX uk_repayment_schedules_one_active_per_loan
    ON repayment_schedules (loan_id) WHERE status = 'ACTIVE';
CREATE INDEX idx_repayment_schedules_loan_created ON repayment_schedules (loan_id, created_at DESC);

CREATE TABLE installments
(
    id                  UUID PRIMARY KEY,
    schedule_id         UUID          NOT NULL,
    loan_id             UUID          NOT NULL,
    installment_number  INTEGER       NOT NULL,
    due_date            DATE          NOT NULL,
    principal_amount    NUMERIC(19, 2) NOT NULL,
    interest_amount     NUMERIC(19, 2) NOT NULL,
    total_amount        NUMERIC(19, 2) NOT NULL,
    remaining_principal NUMERIC(19, 2) NOT NULL,
    status              VARCHAR(20)   NOT NULL,
    currency            VARCHAR(3)    NOT NULL,
    CONSTRAINT fk_installments_schedule_loan FOREIGN KEY (schedule_id, loan_id)
        REFERENCES repayment_schedules (id, loan_id),
    CONSTRAINT uk_installments_schedule_number UNIQUE (schedule_id, installment_number),
    CONSTRAINT ck_installments_number CHECK (installment_number > 0),
    CONSTRAINT ck_installments_amounts CHECK (
        principal_amount >= 0 AND interest_amount >= 0 AND remaining_principal >= 0
        AND total_amount > 0 AND total_amount = principal_amount + interest_amount
    ),
    CONSTRAINT ck_installments_currency CHECK (length(currency) = 3 AND currency = upper(currency)),
    CONSTRAINT ck_installments_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED'))
);
CREATE INDEX idx_installments_loan_due_date ON installments (loan_id, due_date, installment_number);
CREATE INDEX idx_installments_status_due_date ON installments (status, due_date);
