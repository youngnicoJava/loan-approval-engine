package com.loanorigination.repayment.adapter.in.rest.dto;
import com.loanorigination.repayment.domain.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
public record RepaymentScheduleResponse(UUID id,UUID loanId,AmortizationType amortizationType,BigDecimal principal,String currency,
        BigDecimal annualInterestRatePercentage,int termMonths,LocalDate disbursedOn,RepaymentScheduleStatus status,
        Instant createdAt,int installmentCount){
    public static RepaymentScheduleResponse from(RepaymentSchedule s){return new RepaymentScheduleResponse(s.id(),s.loanId(),s.amortizationType(),s.principal().amount(),s.principal().currency().getCurrencyCode(),s.interestRate().percentage(),s.term().months(),s.disbursedOn(),s.status(),s.createdAt(),s.installments().size());}
}
