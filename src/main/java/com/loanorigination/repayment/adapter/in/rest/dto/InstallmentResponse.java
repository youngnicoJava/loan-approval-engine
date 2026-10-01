package com.loanorigination.repayment.adapter.in.rest.dto;
import com.loanorigination.repayment.domain.Installment;
import com.loanorigination.repayment.domain.InstallmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public record InstallmentResponse(UUID id,UUID loanId,int installmentNumber,LocalDate dueDate,BigDecimal principalAmount,
        BigDecimal interestAmount,BigDecimal totalAmount,BigDecimal remainingPrincipal,String currency,InstallmentStatus status){
    public static InstallmentResponse from(Installment i){return new InstallmentResponse(i.id(),i.loanId(),i.installmentNumber(),i.dueDate(),i.principalAmount().amount(),i.interestAmount().amount(),i.totalAmount().amount(),i.remainingPrincipal().amount(),i.totalAmount().currency().getCurrencyCode(),i.status());}
}
