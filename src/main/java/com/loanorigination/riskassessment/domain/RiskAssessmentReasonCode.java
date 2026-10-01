package com.loanorigination.riskassessment.domain;

/**
 * Código estructurado de la razón de la decisión.
 *
 * No guardamos solamente un texto libre porque estos códigos pueden
 * ser utilizados posteriormente para auditoría, métricas y reporting.
 */
public enum RiskAssessmentReasonCode {

    APPROVED_BY_DEVELOPMENT_POLICY,
    REFERRED_BY_DEVELOPMENT_POLICY,
    REJECTED_BY_DEVELOPMENT_POLICY
}