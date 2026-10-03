CREATE TABLE fraud_assessments (
    id UUID PRIMARY KEY,
    assessment_request_id UUID NOT NULL UNIQUE,
    loan_application_id UUID NOT NULL REFERENCES loan_applications(id),
    decision VARCHAR(10) NOT NULL CHECK (decision IN ('PASS', 'REVIEW', 'BLOCK')),
    fraud_score SMALLINT NOT NULL CHECK (fraud_score BETWEEN 0 AND 100),
    risk_level VARCHAR(10) NOT NULL CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH')),
    reason_codes JSONB NOT NULL,
    ruleset_id VARCHAR(80) NOT NULL,
    ruleset_version VARCHAR(40) NOT NULL,
    evaluated_at TIMESTAMPTZ NOT NULL,
    correlation_id VARCHAR(128) NOT NULL
);
CREATE INDEX idx_fraud_assessments_application_recent ON fraud_assessments(loan_application_id, evaluated_at DESC);
