package com.loanorigination.audit;

import com.loanorigination.audit.application.service.WorkflowEventRecorder;
import com.loanorigination.audit.domain.AuditAction;
import com.loanorigination.loanapplication.application.port.out.LoanApplicationRepository;
import com.loanorigination.loanapplication.domain.LoanApplication;
import com.loanorigination.loanapplication.domain.LoanProductType;
import com.loanorigination.shared.domain.LoanTerm;
import com.loanorigination.shared.domain.Money;
import jakarta.enterprise.context.control.ActivateRequestContext;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class WorkflowEventAtomicityTest {
    @Inject WorkflowEventRecorder recorder;
    @Inject EntityManager entityManager;
    @Inject UserTransaction transaction;
    @Inject LoanApplicationRepository applications;

    @Test @ActivateRequestContext
    void auditAndOutboxShareTheBusinessTransaction() throws Exception {
        UUID customer=UUID.randomUUID();
        transaction.begin();
        entityManager.createNativeQuery("insert into customers(id,external_identity_id,full_name,email,status,created_at) values (:id,:identity,'Atomicity Test','atomicity@example.invalid','ACTIVE',:now)")
                .setParameter("id",customer).setParameter("identity","test-"+customer).setParameter("now",Instant.now()).executeUpdate();
        transaction.commit();

        var submitted=application(customer);
        UUID aggregate=submitted.id();
        transaction.begin();
        submitted.submit(Instant.now());applications.save(submitted);
        recorder.record("LoanApplicationSubmitted",AuditAction.APPLICATION_SUBMITTED,"LoanApplication",aggregate,
                Map.of("loanApplicationId",aggregate.toString()));
        transaction.commit();
        assertEquals(1,countApplication(aggregate));
        assertEquals(1,count("audit_events",aggregate));
        assertEquals(1,count("outbox_events",aggregate));

        var rolledBackApplication=application(customer);
        UUID rolledBack=rolledBackApplication.id();
        transaction.begin();
        rolledBackApplication.submit(Instant.now());applications.save(rolledBackApplication);
        recorder.record("LoanApplicationSubmitted",AuditAction.APPLICATION_SUBMITTED,"LoanApplication",rolledBack,
                Map.of("loanApplicationId",rolledBack.toString()));
        transaction.rollback();
        assertEquals(0,countApplication(rolledBack));
        assertEquals(0,count("audit_events",rolledBack));
        assertEquals(0,count("outbox_events",rolledBack));

        transaction.begin();
        entityManager.createNativeQuery("delete from loan_applications where customer_id=:id").setParameter("id",customer).executeUpdate();
        entityManager.createNativeQuery("delete from customers where id=:id").setParameter("id",customer).executeUpdate();
        transaction.commit();
    }

    private LoanApplication application(UUID customer){
        var application=LoanApplication.create(customer,LoanProductType.PERSONAL_LOAN,
                Money.of(new BigDecimal("10000.00"),Currency.getInstance("ARS")),LoanTerm.ofMonths(12),
                "atomicity-test",Instant.now());
        return application;
    }

    private long countApplication(UUID id) throws Exception {
        transaction.begin();
        try {long value=((Number)entityManager.createNativeQuery("select count(*) from loan_applications where id=:id").setParameter("id",id).getSingleResult()).longValue();transaction.commit();return value;}
        catch(Exception e){transaction.rollback();throw e;}
    }

    private long count(String table,UUID aggregate) throws Exception {
        transaction.begin();
        try { long value=((Number)entityManager.createNativeQuery("select count(*) from "+table+" where aggregate_id=:id").setParameter("id",aggregate).getSingleResult()).longValue();transaction.commit();return value; }
        catch(Exception e){transaction.rollback();throw e;}
    }
}
