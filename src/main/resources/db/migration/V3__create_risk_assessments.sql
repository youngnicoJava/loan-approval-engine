CREATE TABLE risk_assessments
(
    id                   UUID PRIMARY KEY,
    loan_application_id  UUID        NOT NULL,
    decision             VARCHAR(20) NOT NULL,
    reason_code          VARCHAR(80) NOT NULL,
    source               VARCHAR(40) NOT NULL,
    assessed_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_risk_assessments_loan_application
        FOREIGN KEY (loan_application_id)
            REFERENCES loan_applications (id),

    CONSTRAINT ck_risk_assessments_decision
        CHECK (
            decision IN (
                         'APPROVE',
                         'REJECT',
                         'REFER'
                )
            ),

    CONSTRAINT ck_risk_assessments_source
        CHECK (
            source IN (
                       'LOCAL_RULES',
                       'CREDIT_RISK_ENGINE'
                )
            )
);

CREATE INDEX idx_risk_assessments_application_date
    ON risk_assessments (
                         loan_application_id,
                         assessed_at DESC
        );