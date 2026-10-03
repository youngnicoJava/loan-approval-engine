# Fraud Detection integration

Loan Origination remains the workflow owner. When its external assessment action runs, it writes two independent request events in the same transaction: existing Credit Risk v2 and Fraud Detection v1. Both use the application ID as assessmentRequestId and preserve X-Correlation-ID. Fraud does not call Credit Risk, and Credit Risk does not call Fraud.

LO persists fraud results separately in `fraud_assessments`; duplicate assessment IDs replay without another transition. Only Credit APPROVE plus Fraud PASS changes UNDER_REVIEW to APPROVED. Credit REFER remains available for manual credit review, but its approve endpoint still requires Fraud PASS. Fraud REVIEW and BLOCK remain in UNDER_REVIEW and cannot issue an offer. Credit REJECT is applied by LO. Late assessment results are stored and audited without changing an application that is no longer UNDER_REVIEW.

Enable with `RISK_ASSESSMENT_MODE=KAFKA`, `RISK_KAFKA_ENABLED=true`, `FRAUD_ASSESSMENT_MODE=KAFKA`, `FRAUD_KAFKA_ENABLED=true`, and `KAFKA_BOOTSTRAP_SERVERS`. Local Compose Kafka may use Credit Risk's broker at localhost:29092; Fraud standalone uses localhost:29093.
