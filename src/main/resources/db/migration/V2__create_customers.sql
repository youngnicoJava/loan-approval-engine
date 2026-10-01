CREATE TABLE customers
(
    id                   UUID PRIMARY KEY,
    external_identity_id VARCHAR(255) NOT NULL,
    full_name            VARCHAR(150) NOT NULL,
    email                VARCHAR(320) NOT NULL,
    status               VARCHAR(20)  NOT NULL,
    created_at           TIMESTAMPTZ  NOT NULL,

    CONSTRAINT uk_customers_external_identity_id
        UNIQUE (external_identity_id),

    CONSTRAINT ck_customers_status
        CHECK (
            status IN ('ACTIVE', 'BLOCKED')
            )
);

ALTER TABLE loan_applications
    ADD CONSTRAINT fk_loan_applications_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers (id);