package com.loanorigination.idempotency.application.service;

import com.loanorigination.idempotency.application.exception.*;
import com.loanorigination.idempotency.application.port.out.IdempotencyRepository;
import com.loanorigination.idempotency.application.port.out.ResponseCodecPort;
import com.loanorigination.idempotency.domain.*;
import com.loanorigination.security.application.port.out.CurrentUserPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.time.Clock;
import java.time.Duration;
import java.util.function.Supplier;

@ApplicationScoped
public class IdempotencyService {
 private final IdempotencyRepository records; private final ResponseCodecPort codec; private final CurrentUserPort currentUser; private final Clock clock;
 @Inject public IdempotencyService(IdempotencyRepository records,ResponseCodecPort codec,CurrentUserPort currentUser,Clock clock){this.records=records;this.codec=codec;this.currentUser=currentUser;this.clock=clock;}
 public <T> Result<T> execute(String scope,String key,String hash,int responseStatus,Class<T> type,Supplier<T> operation){
        var claim=claim(scope,key,hash);var record=claim.record();
  if(!record.requestHash().equals(hash))throw new IdempotencyKeyReusedException();
  if(record.status()==IdempotencyStatus.COMPLETED)return new Result<>(codec.decode(record.responseBody(),type),record.responseStatus(),true);
  if(!claim.acquired())throw new IdempotencyInProgressException();
  T value=operation.get();records.complete(record,responseStatus,codec.encode(value));return new Result<>(value,responseStatus,false);
 }
 @Transactional(TxType.REQUIRES_NEW)
 public <T> ExternalClaim<T> beginExternal(String scope,String key,String hash,Class<T> type){
  var claim=claim(scope,key,hash,Duration.ofMinutes(2));var record=claim.record();
  if(!record.requestHash().equals(hash))throw new IdempotencyKeyReusedException();
  if(record.status()==IdempotencyStatus.COMPLETED)return new ExternalClaim<>(record,codec.decode(record.responseBody(),type),record.responseStatus());
  if(!claim.acquired())throw new IdempotencyInProgressException();return new ExternalClaim<>(record,null,null);
 }
 @Transactional(TxType.REQUIRES_NEW) public void completeExternal(ExternalClaim<?> claim,int status,Object body){records.complete(claim.record(),status,codec.encode(body));}
 @Transactional(TxType.REQUIRES_NEW) public void releaseExternal(ExternalClaim<?> claim){records.delete(claim.record());}
 private IdempotencyClaim claim(String scope,String key,String hash){return claim(scope,key,hash,Duration.ofHours(24));}
 private IdempotencyClaim claim(String scope,String key,String hash,Duration expiry){
  if(key==null||key.isBlank()||key.length()>255||!key.matches("[A-Za-z0-9._:-]{1,255}"))throw new IdempotencyKeyRequiredException();
  var now=clock.instant();return records.claim(currentUser.getCurrentUser().externalIdentityId(),scope,key,hash,now,now.plus(expiry));
 }
 public record Result<T>(T body,int status,boolean replayed) { }
 public record ExternalClaim<T>(IdempotencyRecord record,T cachedBody,Integer cachedStatus){public boolean completed(){return cachedBody!=null;}}
}
