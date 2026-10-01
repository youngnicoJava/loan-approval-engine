package com.loanorigination.shared.adapter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanorigination.shared.application.port.out.PayloadCodecPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class JacksonPayloadCodecAdapter implements PayloadCodecPort {
    private final ObjectMapper mapper;
    @Inject public JacksonPayloadCodecAdapter(ObjectMapper mapper){this.mapper=mapper;}
    @Override public String encode(Map<String,Object> payload){try{return mapper.writeValueAsString(payload);}catch(Exception e){throw new IllegalStateException("Cannot serialize event payload",e);}}
}
