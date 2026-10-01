package com.loanorigination.riskassessment.domain;

/**
 * Identifica quién produjo la evaluación de riesgo.
 *
 * LOCAL_RULES se utiliza únicamente como implementación inicial
 * para que Loan Origination funcione de forma autónoma.
 *
 * CREDIT_RISK_ENGINE será utilizado cuando integremos el servicio
 * independiente de Credit Risk.
 */
public enum RiskAssessmentSource {

    LOCAL_RULES,
    CREDIT_RISK_ENGINE
}