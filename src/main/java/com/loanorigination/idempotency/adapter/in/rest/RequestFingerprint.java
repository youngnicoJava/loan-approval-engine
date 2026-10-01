package com.loanorigination.idempotency.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.security.MessageDigest;
import java.util.HexFormat;

@ApplicationScoped
public class RequestFingerprint {
 private final ObjectMapper mapper;
 @Inject public RequestFingerprint(ObjectMapper mapper){this.mapper=mapper;}
 public String of(Object request){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(mapper.writeValueAsBytes(request)));}catch(Exception e){throw new IllegalStateException("Cannot fingerprint request",e);}}
}
