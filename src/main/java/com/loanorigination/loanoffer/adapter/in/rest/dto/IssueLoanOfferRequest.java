package com.loanorigination.loanoffer.adapter.in.rest.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
public record IssueLoanOfferRequest(@DecimalMin(value="0.01", inclusive=true) @Digits(integer=17,fraction=2) BigDecimal offeredPrincipal,
                                    @Positive Integer offeredTermMonths,
                                    @NotNull @DecimalMin("0.0") @Digits(integer=5,fraction=4) BigDecimal annualInterestRatePercentage,
                                    @NotNull @Future Instant expiresAt) {}
