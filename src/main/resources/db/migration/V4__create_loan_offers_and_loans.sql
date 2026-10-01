CREATE TABLE loan_offers
(
    id                              UUID PRIMARY KEY,
    loan_application_id             UUID          NOT NULL REFERENCES loan_applications (id),
    customer_id                     UUID          NOT NULL REFERENCES customers (id),
    principal                       NUMERIC(19, 2) NOT NULL,
    currency                        VARCHAR(3)    NOT NULL,
    term_months                     INTEGER       NOT NULL,
    annual_interest_rate_percentage NUMERIC(9, 4) NOT NULL,
    monthly_installment             NUMERIC(19, 2) NOT NULL,
    total_repayment                 NUMERIC(19, 2) NOT NULL,
    status                          VARCHAR(20)   NOT NULL,
    created_at                      TIMESTAMPTZ   NOT NULL,
    expires_at                      TIMESTAMPTZ   NOT NULL,
    accepted_at                     TIMESTAMPTZ,
    declined_at                     TIMESTAMPTZ,
    CONSTRAINT ck_loan_offers_amounts CHECK (principal > 0 AND monthly_installment > 0 AND total_repayment > 0),
    CONSTRAINT ck_loan_offers_term CHECK (term_months > 0),
    CONSTRAINT ck_loan_offers_rate CHECK (annual_interest_rate_percentage >= 0),
    CONSTRAINT ck_loan_offers_currency CHECK (length(currency) = 3 AND currency = upper(currency)),
    CONSTRAINT ck_loan_offers_status CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED')),
    CONSTRAINT ck_loan_offers_expiry CHECK (expires_at > created_at)
);

-- Historical declined/expired offers are retained. Only one offer awaiting a decision
-- or already accepted may exist for an application.
CREATE UNIQUE INDEX uk_loan_offers_one_live_per_application
    ON loan_offers (loan_application_id)
    WHERE status IN ('PENDING', 'ACCEPTED');
CREATE INDEX idx_loan_offers_customer_created ON loan_offers (customer_id, created_at DESC);

CREATE TABLE loans
(
    id                              UUID PRIMARY KEY,
    loan_offer_id                   UUID          NOT NULL UNIQUE REFERENCES loan_offers (id),
    loan_application_id             UUID          NOT NULL UNIQUE REFERENCES loan_applications (id),
    customer_id                     UUID          NOT NULL REFERENCES customers (id),
    principal                       NUMERIC(19, 2) NOT NULL,
    currency                        VARCHAR(3)    NOT NULL,
    term_months                     INTEGER       NOT NULL,
    annual_interest_rate_percentage NUMERIC(9, 4) NOT NULL,
    monthly_installment             NUMERIC(19, 2) NOT NULL,
    total_repayment                 NUMERIC(19, 2) NOT NULL,
    status                          VARCHAR(30)   NOT NULL,
    created_at                      TIMESTAMPTZ   NOT NULL,
    activated_at                    TIMESTAMPTZ,
    paid_off_at                     TIMESTAMPTZ,
    CONSTRAINT ck_loans_amounts CHECK (principal > 0 AND monthly_installment > 0 AND total_repayment > 0),
    CONSTRAINT ck_loans_term CHECK (term_months > 0),
    CONSTRAINT ck_loans_rate CHECK (annual_interest_rate_percentage >= 0),
    CONSTRAINT ck_loans_currency CHECK (length(currency) = 3 AND currency = upper(currency)),
    CONSTRAINT ck_loans_status CHECK (status IN ('PENDING_DISBURSEMENT', 'ACTIVE', 'PAID_OFF', 'DEFAULTED', 'CANCELLED'))
);
CREATE INDEX idx_loans_customer_created ON loans (customer_id, created_at DESC);
