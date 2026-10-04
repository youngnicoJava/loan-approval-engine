package com.loanorigination.loanapplication.application.port.out;

import com.loanorigination.loanapplication.domain.LoanApplication;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import com.loanorigination.loanapplication.domain.LoanApplicationStatus;

/**
 * Puerto de salida para persistencia de solicitudes.
 *
 * La aplicación conoce este contrato, pero no sabe si debajo existe
 * PostgreSQL, otra base, memoria o cualquier otro mecanismo.
 */
public interface LoanApplicationRepository {

    void save(LoanApplication loanApplication);

    Optional<LoanApplication> findById(UUID loanApplicationId);

    Optional<LoanApplication> findByIdForUpdate(UUID loanApplicationId);

    List<LoanApplication> findByCustomerId(UUID customerId);

    List<LoanApplication> findForQueue(LoanApplicationStatus status, int offset, int limit);
}
