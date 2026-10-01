package com.loanorigination.riskassessment.application.port.out;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.domain.RiskAssessmentResult;

/**
 * Puerto hacia el sistema que calcula el riesgo.
 *
 * Loan Origination conoce el contrato, pero no sabe si debajo existe:
 *
 * - reglas locales;
 * - una API HTTP;
 * - otro microservicio;
 * - eventualmente Kafka.
 *
 * Esta abstracción permite cambiar el mecanismo de evaluación
 * sin modificar el dominio ni los casos de uso.
 */
public interface RiskAssessmentPort {

    RiskAssessmentResult evaluate(
            LoanApplication loanApplication
    );
}