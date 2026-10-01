CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(120) NOT NULL,
    event_version INTEGER NOT NULL,
    aggregate_type VARCHAR(60) NOT NULL,
    aggregate_id UUID NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    payload TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    locked_at TIMESTAMPTZ,
    last_error VARCHAR(1000),
    CONSTRAINT ck_outbox_version CHECK (event_version > 0),
    CONSTRAINT ck_outbox_attempts CHECK (attempt_count >= 0),
    CONSTRAINT ck_outbox_status CHECK (status IN ('PENDING','PROCESSING','PUBLISHED','FAILED'))
);
CREATE INDEX idx_outbox_pending ON outbox_events (occurred_at) WHERE status IN ('PENDING','FAILED');
CREATE INDEX idx_outbox_processing_lease ON outbox_events (locked_at) WHERE status = 'PROCESSING';
CREATE INDEX idx_outbox_aggregate ON outbox_events (aggregate_id, occurred_at);
