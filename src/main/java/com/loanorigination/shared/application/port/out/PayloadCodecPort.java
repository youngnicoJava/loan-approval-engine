package com.loanorigination.shared.application.port.out;

import java.util.Map;

public interface PayloadCodecPort { String encode(Map<String,Object> payload); }
