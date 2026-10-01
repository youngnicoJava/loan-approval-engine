package com.loanorigination.loanoffer.application.port.in;
import com.loanorigination.loanoffer.domain.LoanOffer;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public interface IssueLoanOfferUseCase { LoanOffer issue(UUID applicationId, BigDecimal offeredPrincipal, Integer offeredTermMonths, BigDecimal annualRatePercentage, Instant expiresAt); }
