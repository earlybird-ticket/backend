package com.earlybirdticket.common.aop;

import org.springframework.stereotype.Component;

@Component
public class LoggingStatusManager {

    private final ThreadLocal<LoggingStatus> statusContainer = new ThreadLocal<>();

    //ThreadLocal 가져오기(로깅 상태 확인용)
    public LoggingStatus getExistingLoggingStatus() {
        LoggingStatus status = statusContainer.get();
        if (status == null) {
            throw new IllegalArgumentException("ThreadLocal LoggingStatus not Exist");
        }
        return status;
    }

    public void syncStatus() {
        if (statusContainer.get() == null) {
            statusContainer.set(new LoggingStatus());
        }
    }

    public void clearResource() {
        statusContainer.remove();
    }
}
