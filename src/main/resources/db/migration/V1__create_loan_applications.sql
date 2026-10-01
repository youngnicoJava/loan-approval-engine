CREATE TABLE loan_applications
(
    id               UUID PRIMARY KEY,
    customer_id      UUID         NOT NULL,
    product_type     VARCHAR(50)  NOT NULL,
    requested_amount NUMERIC(19, 2) NOT NULL,
    currency         VARCHAR(3)   NOT NULL,
    term_months      INTEGER      NOT NULL,
    purpose          VARCHAR(500) NOT NULL,
    status           VARCHAR(30)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL,
    submitted_at     TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ  NOT NULL,

    CONSTRAINT ck_loan_applications_product_type
        CHECK (product_type IN ('PERSONAL_LOAN')),

    CONSTRAINT ck_loan_applications_requested_amount
        CHECK (requested_amount > 0),

    CONSTRAINT ck_loan_applications_currency
        CHECK (
            length(currency) = 3
                AND currency = upper(currency)
            ),

    CONSTRAINT ck_loan_applications_term
        CHECK (term_months > 0),

    CONSTRAINT ck_loan_applications_status
        CHECK (
            status IN (
                       'DRAFT',
                       'SUBMITTED',
                       'UNDER_REVIEW',
                       'APPROVED',
                       'REJECTED',
                       'CANCELLED'
                )
            )
);

CREATE INDEX idx_loan_applications_customer_id
    ON loan_applications (customer_id);

CREATE INDEX idx_loan_applications_status
    ON loan_applications (status);