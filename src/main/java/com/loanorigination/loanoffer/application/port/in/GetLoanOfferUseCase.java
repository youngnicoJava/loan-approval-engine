package com.loanorigination.loanoffer.application.port.in;
import com.loanorigination.loanoffer.domain.LoanOffer;
import java.util.UUID;
public interface GetLoanOfferUseCase {
    LoanOffer get(UUID id);
    LoanOffer getByApplicationId(UUID applicationId);
}
