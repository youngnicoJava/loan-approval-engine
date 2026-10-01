CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    actor_id VARCHAR(255) NOT NULL,
    actor_type VARCHAR(30) NOT NULL,
    action VARCHAR(50) NOT NULL,
    aggregate_type VARCHAR(60) NOT NULL,
    aggregate_id UUID NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    metadata TEXT NOT NULL,
    CONSTRAINT ck_audit_actor_type CHECK (actor_type IN ('CUSTOMER','LOAN_OFFICER','AUDITOR','ADMIN','SYSTEM')),
    CONSTRAINT ck_audit_action CHECK (action IN ('APPLICATION_SUBMITTED','RISK_ASSESSMENT_COMPLETED','APPLICATION_APPROVED','APPLICATION_REJECTED','OFFER_CREATED','OFFER_ACCEPTED','LOAN_CREATED','DISBURSEMENT_REQUESTED','LOAN_DISBURSED','REPAYMENT_SCHEDULE_CREATED'))
);
CREATE INDEX idx_audit_events_aggregate ON audit_events (aggregate_id, occurred_at DESC);
CREATE INDEX idx_audit_events_occurred_at ON audit_events (occurred_at DESC);
CREATE INDEX idx_audit_events_actor ON audit_events (actor_id, occurred_at DESC);
CREATE INDEX idx_audit_events_correlation ON audit_events (correlation_id);
