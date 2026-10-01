package com.loanorigination.riskassessment.adapter.out.local;

import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.riskassessment.application.port.out.RiskAssessmentPort;
import com.loanorigination.riskassessment.domain.RiskAssessmentReasonCode;
import com.loanorigination.riskassessment.domain.RiskAssessmentResult;
import com.loanorigination.riskassessment.domain.RiskAssessmentSource;
import com.loanorigination.riskassessment.domain.RiskDecision;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

/**
 * Implementación autónoma del motor de riesgo para desarrollo.
 *
 * Estas reglas NO representan una política crediticia bancaria real.
 * Su objetivo es permitir que Loan Origination funcione standalone
 * mientras el Credit Risk Engine todavía no existe.
 *
 * Las reglas están aisladas en este adapter para que puedan desaparecer
 * posteriormente sin modificar el dominio ni los casos de uso.
 */
@ApplicationScoped
public class LocalRiskAssessmentAdapter implements RiskAssessmentPort {

    private static final String ARS = "ARS";

    private static final BigDecimal AUTO_APPROVAL_LIMIT =
            new BigDecimal("2000000");

    private static final BigDecimal MANUAL_REVIEW_LIMIT =
            new BigDecimal("10000000");

    private static final int AUTO_APPROVAL_MAX_TERM_MONTHS = 36;

    private static final int MANUAL_REVIEW_MAX_TERM_MONTHS = 60;

    @Override
    public RiskAssessmentResult evaluate(
            LoanApplication loanApplication
    ) {

        /*
         * Esta implementación deliberadamente evita fingir un score
         * financiero real. Solamente modela el boundary entre Origination
         * y un motor externo.
         */
        if (!ARS.equals(
                loanApplication.requestedAmount()
                        .currency()
                        .getCurrencyCode()
        )) {

            return new RiskAssessmentResult(
                    RiskDecision.REFER,
                    RiskAssessmentReasonCode
                            .REFERRED_BY_DEVELOPMENT_POLICY,
                    RiskAssessmentSource.LOCAL_RULES
            );
        }

        BigDecimal amount =
                loanApplication.requestedAmount().amount();

        int termMonths =
                loanApplication.term().months();

        if (amount.compareTo(AUTO_APPROVAL_LIMIT) <= 0
                && termMonths <= AUTO_APPROVAL_MAX_TERM_MONTHS) {

            return new RiskAssessmentResult(
                    RiskDecision.APPROVE,
                    RiskAssessmentReasonCode
                            .APPROVED_BY_DEVELOPMENT_POLICY,
                    RiskAssessmentSource.LOCAL_RULES
            );
        }

        if (amount.compareTo(MANUAL_REVIEW_LIMIT) <= 0
                && termMonths <= MANUAL_REVIEW_MAX_TERM_MONTHS) {

            return new RiskAssessmentResult(
                    RiskDecision.REFER,
                    RiskAssessmentReasonCode
                            .REFERRED_BY_DEVELOPMENT_POLICY,
                    RiskAssessmentSource.LOCAL_RULES
            );
        }

        return new RiskAssessmentResult(
                RiskDecision.REJECT,
                RiskAssessmentReasonCode
                        .REJECTED_BY_DEVELOPMENT_POLICY,
                RiskAssessmentSource.LOCAL_RULES
        );
    }
}