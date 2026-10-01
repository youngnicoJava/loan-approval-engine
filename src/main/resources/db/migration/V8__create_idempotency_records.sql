CREATE TABLE idempotency_records (
    id UUID PRIMARY KEY,
    actor_id VARCHAR(255) NOT NULL,
    scope VARCHAR(160) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    request_hash CHAR(64) NOT NULL,
    response_status INTEGER,
    response_body TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_idempotency_actor_scope_key UNIQUE (actor_id, scope, idempotency_key),
    CONSTRAINT ck_idempotency_status CHECK (status IN ('PROCESSING','COMPLETED')),
    CONSTRAINT ck_idempotency_response CHECK ((status = 'PROCESSING' AND response_status IS NULL AND response_body IS NULL) OR (status = 'COMPLETED' AND response_status BETWEEN 100 AND 599 AND response_body IS NOT NULL))
);
CREATE INDEX idx_idempotency_expiry ON idempotency_records (expires_at);
