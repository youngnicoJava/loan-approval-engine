package com.loanorigination.idempotency.application.service;

import com.loanorigination.idempotency.application.exception.IdempotencyKeyReusedException;
import com.loanorigination.idempotency.application.port.out.IdempotencyRepository;
import com.loanorigination.idempotency.application.port.out.ResponseCodecPort;
import com.loanorigination.idempotency.domain.*;
import com.loanorigination.security.domain.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyServiceTest {
    @Test void sameKeyAndRequestReplaysTheStoredResult() {
        var repository=new MemoryRepository();
        ResponseCodecPort codec=new ResponseCodecPort(){public String encode(Object value){return value.toString();}public <T>T decode(String value,Class<T> type){return type.cast(value);}};
        var service=new IdempotencyService(repository,codec,
                ()->new AuthenticatedUser("customer-1",java.util.Set.of("CUSTOMER")),
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC), metric -> {});
        var executions=new AtomicInteger();
        var first=service.execute("scope","key-1","same-hash",201,String.class,()->"created-1"+executions.incrementAndGet());
        var retry=service.execute("scope","key-1","same-hash",201,String.class,()->"should-not-run");
        assertEquals("created-11",first.body());
        assertEquals(first.body(),retry.body());
        assertEquals(201,retry.status());
        assertTrue(retry.replayed());
        assertEquals(1,executions.get());
    }

    @Test void sameKeyWithDifferentRequestConflicts() {
        var service=new IdempotencyService(new MemoryRepository(),new ResponseCodecPort(){public String encode(Object value){return value.toString();}public <T>T decode(String value,Class<T> type){return type.cast(value);}},
                ()->new AuthenticatedUser("customer-1",java.util.Set.of("CUSTOMER")),Clock.systemUTC(), metric -> {});
        service.execute("scope","key-2","hash-a",200,String.class,()->"done");
        assertThrows(IdempotencyKeyReusedException.class,()->service.execute("scope","key-2","hash-b",200,String.class,()->"again"));
    }

    private static class MemoryRepository implements IdempotencyRepository {
        private final Map<String,IdempotencyRecord> data=new HashMap<>();
        @Override public IdempotencyClaim claim(String actor,String scope,String key,String hash,Instant now,Instant expires){
            String mapKey=actor+"|"+scope+"|"+key;var record=data.get(mapKey);
            if(record==null||record.expiredAt(now)){record=new IdempotencyRecord(UUID.randomUUID(),actor,scope,key,hash,null,null,IdempotencyStatus.PROCESSING,now,expires);data.put(mapKey,record);return new IdempotencyClaim(record,true);}
            return new IdempotencyClaim(record,false);
        }
        @Override public void complete(IdempotencyRecord record,int status,String body){data.put(record.actorId()+"|"+record.scope()+"|"+record.key(),new IdempotencyRecord(record.id(),record.actorId(),record.scope(),record.key(),record.requestHash(),status,body,IdempotencyStatus.COMPLETED,record.createdAt(),record.expiresAt()));}
        @Override public void delete(IdempotencyRecord record){data.remove(record.actorId()+"|"+record.scope()+"|"+record.key());}
    }
}
