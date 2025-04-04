package com.earlybirdticket.common.util;

import java.util.UUID;
import org.slf4j.MDC;


/**
 * 로컬 쓰레드 기반 분산 로그 수집 데이터 입니다. traceId로 흐름을 추적할 수 있습니다! 해당 쓰레드의 traceId는 삭제해주지 않으면 다음 사용자가 사용할 때
 * 데이터가 누적되기 때문에 clear()가 필요합니다. MDC.clear()를 수행하면 메모리 누수를 방지할 수 있습니다.
 *
 * @author sunro
 */
public class MDCTraceUtils {

    public static final String TRACE_ID = "traceId"; //추적아이디

    public static void setTraceIdIfAbsent() {
        if (MDC.get(TRACE_ID) == null) {
            MDC.put(TRACE_ID, UUID.randomUUID().toString());
        }
    }

    public static void clear() {
        MDC.clear();
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }
}