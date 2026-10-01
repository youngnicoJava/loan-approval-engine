package com.loanorigination.repayment.adapter.out.calculation;

import com.loanorigination.loan.domain.Loan;
import com.loanorigination.repayment.application.port.out.AmortizationCalculatorPort;
import com.loanorigination.repayment.domain.*;
import com.loanorigination.shared.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/** Implementa el sistema francés; el interés de cada período se calcula sobre saldo insoluto. */
@ApplicationScoped
public class FixedPaymentAmortizationCalculator implements AmortizationCalculatorPort {
    @Override public AmortizationQuote calculateQuote(Money principal,InterestRate rate,LoanTerm term){
        var result=calculate(principal,rate,term,null,null,null);
        return new AmortizationQuote(result.regularPayment(),result.totalRepayment());
    }
    @Override public RepaymentSchedule generateSchedule(Loan loan,Instant disbursedAt,Instant createdAt){
        LocalDate disbursedOn=disbursedAt.atZone(ZoneOffset.UTC).toLocalDate();
        var result=calculate(loan.principal(),loan.annualInterestRate(),loan.term(),loan.id(),disbursedOn,createdAt);
        return RepaymentSchedule.create(loan.id(),AmortizationType.FRENCH_FIXED_PAYMENT,loan.principal(),
                loan.annualInterestRate(),loan.term(),disbursedOn,result.installments(),createdAt);
    }
    private Calculation calculate(Money principal,InterestRate annualRate,LoanTerm term,java.util.UUID loanId,LocalDate disbursedOn,Instant createdAt){
        var currency=principal.currency();int scale=FinancialRounding.scale(currency);
        BigDecimal p=principal.amount();if(p.signum()<=0)throw new IllegalArgumentException("Principal must be positive");
        BigDecimal monthlyRate=annualRate.monthlyPeriodicRate(FinancialRounding.CALCULATION_CONTEXT);
        int n=term.months();BigDecimal regular;
        if(monthlyRate.signum()==0) regular=p.divide(BigDecimal.valueOf(n),scale,FinancialRounding.MONETARY_ROUNDING);
        else {
            BigDecimal factor=BigDecimal.ONE.add(monthlyRate,FinancialRounding.CALCULATION_CONTEXT).pow(n,FinancialRounding.CALCULATION_CONTEXT);
            regular=p.multiply(monthlyRate,FinancialRounding.CALCULATION_CONTEXT).multiply(factor,FinancialRounding.CALCULATION_CONTEXT)
                    .divide(factor.subtract(BigDecimal.ONE,FinancialRounding.CALCULATION_CONTEXT),scale,FinancialRounding.MONETARY_ROUNDING);
        }
        if(regular.signum()<=0)throw new IllegalArgumentException("Calculated installment must be positive");
        List<Installment> installments=new ArrayList<>();BigDecimal balance=p.setScale(scale,FinancialRounding.MONETARY_ROUNDING);BigDecimal total=BigDecimal.ZERO;
        for(int i=1;i<=n;i++){
            BigDecimal interest=balance.multiply(monthlyRate,FinancialRounding.CALCULATION_CONTEXT).setScale(scale,FinancialRounding.MONETARY_ROUNDING);
            BigDecimal principalPart=(i==n)?balance:regular.subtract(interest).setScale(scale,FinancialRounding.MONETARY_ROUNDING);
            if(principalPart.signum()<=0||principalPart.compareTo(balance)>0)throw new IllegalArgumentException("Rate and term produce an invalid amortization period");
            BigDecimal remaining=balance.subtract(principalPart).setScale(scale,FinancialRounding.MONETARY_ROUNDING);
            BigDecimal installmentTotal=principalPart.add(interest).setScale(scale,FinancialRounding.MONETARY_ROUNDING);
            total=total.add(installmentTotal);
            if(loanId!=null){
                LocalDate dueDate=disbursedOn.plusMonths(i);
                installments.add(Installment.create(loanId,i,dueDate,money(principalPart,currency),money(interest,currency),money(installmentTotal,currency),money(remaining,currency)));
            }
            balance=remaining;
        }
        if(balance.signum()!=0)throw new IllegalStateException("Amortization did not clear principal in the final installment");
        return new Calculation(regular,total,installments);
    }
    private Money money(BigDecimal amount,Currency currency){return Money.of(amount,currency);}
    private record Calculation(BigDecimal regularPayment,BigDecimal totalRepayment,List<Installment> installments){}
}
