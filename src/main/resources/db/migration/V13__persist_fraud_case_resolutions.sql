CREATE TABLE fraud_case_resolutions (
    fraud_case_id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    fraud_assessment_id UUID NOT NULL UNIQUE,
    loan_application_id UUID NOT NULL,
    resolution VARCHAR(24) NOT NULL CHECK (resolution IN ('CLEARED', 'CONFIRMED_FRAUD')),
    resolved_at TIMESTAMPTZ NOT NULL,
    correlation_id VARCHAR(128) NOT NULL
);

CREATE INDEX idx_fraud_case_resolutions_application
    ON fraud_case_resolutions(loan_application_id, resolved_at DESC);
