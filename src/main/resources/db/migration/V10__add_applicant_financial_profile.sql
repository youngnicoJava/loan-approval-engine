ALTER TABLE loan_applications
    ADD COLUMN monthly_income NUMERIC(19, 2),
    ADD COLUMN existing_monthly_debt_obligations NUMERIC(19, 2),
    ADD COLUMN employment_status VARCHAR(30),
    ADD COLUMN employment_tenure_months INTEGER;

ALTER TABLE loan_applications
    ADD CONSTRAINT ck_loan_applications_financial_profile
    CHECK (
        (monthly_income IS NULL AND existing_monthly_debt_obligations IS NULL
            AND employment_status IS NULL AND employment_tenure_months IS NULL)
        OR
        (monthly_income > 0 AND existing_monthly_debt_obligations >= 0
            AND employment_status IN ('PERMANENT', 'SELF_EMPLOYED', 'TEMPORARY', 'UNEMPLOYED')
            AND employment_tenure_months BETWEEN 0 AND 600)
    );
