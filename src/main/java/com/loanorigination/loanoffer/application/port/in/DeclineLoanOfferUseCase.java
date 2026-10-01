package com.loanorigination.loanoffer.application.port.in;
import com.loanorigination.loanoffer.domain.LoanOffer;
import java.util.UUID;
public interface DeclineLoanOfferUseCase { LoanOffer decline(UUID offerId); }
