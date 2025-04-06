package com.earlybird.ticket.common.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingTracer {

    private static final Logger log = LoggerFactory.getLogger(LoggingTracer.class);
    private static final String REQUEST_PREFIX = "--->";
    private static final String RESPONSE_PREFIX = "<---";
    private static final String EXCEPTION_PREFIX = "<X--";

    private final LoggingStatusManager loggingStatusManager;

    public LoggingTracer(LoggingStatusManager loggingStatusManager) {
        this.loggingStatusManager = loggingStatusManager;
    }

    public void methodCall(String methodSignature, Object[] args) {
        loggingStatusManager.syncStatus();
        LoggingStatus status = loggingStatusManager.getExistingLoggingStatus();
        status.increaseDepth();

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("type", "method-entry");
        logMap.put("service", "monitoring-service");
        logMap.put("method", methodSignature);
        logMap.put("args", Arrays.toString(args));
        logMap.put("traceId", MDC.get("traceId"));
        logMap.put("depth", status.depthPrefix(REQUEST_PREFIX));
        logMap.put("timestamp", Instant.now().toString());

        log.info("{}", logMap);
    }

    public void methodReturn(String methodSignature) {
        LoggingStatus status = loggingStatusManager.getExistingLoggingStatus();

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("type", "method-exit");
        logMap.put("service", "monitoring-service");
        logMap.put("method", methodSignature);
        logMap.put("duration_ms", status.calculateTakenTime());
        logMap.put("traceId", MDC.get("traceId"));
        logMap.put("depth", status.depthPrefix(RESPONSE_PREFIX));
        logMap.put("timestamp", Instant.now().toString());

        log.info("{}", logMap);
        status.decreaseDepth();
    }

    public void throwException(String methodSignature, Throwable exception) {
        LoggingStatus status = loggingStatusManager.getExistingLoggingStatus();

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("type", "method-exception");
        logMap.put("service", "monitoring-service");
        logMap.put("method", methodSignature);
        logMap.put("duration_ms", status.calculateTakenTime());
        logMap.put("traceId", MDC.get("traceId"));
        logMap.put("exception", exception.toString());
        logMap.put("depth", status.depthPrefix(EXCEPTION_PREFIX));
        logMap.put("timestamp", Instant.now().toString());

        log.info("{}", logMap);
        status.decreaseDepth();
    }
}