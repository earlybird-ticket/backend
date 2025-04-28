package com.earlybird.ticket.common.aop;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingTracer {

    private static final Logger log = LoggerFactory.getLogger(LoggingTracer.class);
    private static final String REQUEST_PREFIX = "--->";
    private static final String RESPONSE_PREFIX = "<---";
    private static final String EXCEPTION_PREFIX = "<X--";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    private final LoggingStatusManager loggingStatusManager;

    public LoggingTracer(LoggingStatusManager loggingStatusManager) {
        this.loggingStatusManager = loggingStatusManager;
    }

    public final void methodCall(String methodSignature,
                                 Object[] args) {
        loggingStatusManager.syncStatus();
        LoggingStatus status = loggingStatusManager.getExistingLoggingStatus();
        status.increaseDepth();

        Map<String, Object> logMap = commonLogFields("method-entry",
                                                     methodSignature,
                                                     status.depthPrefix(REQUEST_PREFIX));
        logMap.put("args",
                   Arrays.toString(args));

        log.info("{}",
                 logMap);
    }

    public final void methodReturn(String methodSignature,
                                   HttpServletResponse response) {
        LoggingStatus status = loggingStatusManager.getExistingLoggingStatus();

        Map<String, Object> logMap = commonLogFields("method-exit",
                                                     methodSignature,
                                                     status.depthPrefix(RESPONSE_PREFIX));
        logMap.put("duration_ms",
                   status.calculateTakenTime());
        logMap.put("statusCode",
                   response != null ? response.getStatus() : 200);

        log.info("{}",
                 logMap);
        status.decreaseDepth();
    }

    public final void throwException(String methodSignature,
                                     Throwable exception) {
        LoggingStatus status = loggingStatusManager.getExistingLoggingStatus();

        Map<String, Object> logMap = commonLogFields("method-exception",
                                                     methodSignature,
                                                     status.depthPrefix(EXCEPTION_PREFIX));
        logMap.put("duration_ms",
                   status.calculateTakenTime());
        logMap.put("exception",
                   exception.toString());

        log.info("{}",
                 logMap);
        status.decreaseDepth();
    }

    private Map<String, Object> commonLogFields(String type,
                                                String methodSignature,
                                                String depth) {
        Map<String, Object> map = new HashMap<>();
        map.put("type",
                type);
        map.put("service",
                "monitoring-service");
        map.put("method",
                methodSignature);
        map.put("depth",
                depth);
        map.put("timestamp",
                formatter.format(Instant.now()));
        return map;
    }
}