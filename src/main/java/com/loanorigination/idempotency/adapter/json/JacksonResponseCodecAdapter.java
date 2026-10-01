package com.loanorigination.idempotency.adapter.json;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanorigination.idempotency.application.port.out.ResponseCodecPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
@ApplicationScoped public class JacksonResponseCodecAdapter implements ResponseCodecPort {
 private final ObjectMapper mapper;
 @Inject public JacksonResponseCodecAdapter(ObjectMapper mapper){this.mapper=mapper;}
 @Override public String encode(Object value){try{return mapper.writeValueAsString(value);}catch(Exception e){throw new IllegalStateException("Cannot store idempotent response",e);}}
 @Override public <T>T decode(String value,Class<T> type){try{return mapper.readValue(value,type);}catch(Exception e){throw new IllegalStateException("Cannot replay idempotent response",e);}}
}
