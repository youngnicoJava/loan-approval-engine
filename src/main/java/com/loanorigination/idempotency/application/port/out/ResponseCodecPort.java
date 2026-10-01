package com.loanorigination.idempotency.application.port.out;

public interface ResponseCodecPort {
    String encode(Object value);
    <T> T decode(String value, Class<T> type);
}
