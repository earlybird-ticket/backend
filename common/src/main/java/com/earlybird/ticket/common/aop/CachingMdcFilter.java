package com.earlybird.ticket.common.aop;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class CachingMdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        setMdc(httpRequest);
        try {
            chain.doFilter(request,
                           response);
        } finally {
            MDC.clear();
        }
    }

    private void setMdc(HttpServletRequest request) {

        // 기타 요청 정보
        MDC.put(MdcKey.REQUEST_ID.name(),
                UUID.randomUUID()
                    .toString());
        MDC.put(MdcKey.REQUEST_URI.name(),
                request.getRequestURI());
        MDC.put(MdcKey.REQUEST_METHOD.name(),
                request.getMethod());
        MDC.put(MdcKey.REQUEST_PARAMS.name(),
                request.getQueryString());
    }

    public enum MdcKey {
        REQUEST_ID,
        REQUEST_IP,
        REQUEST_METHOD,
        REQUEST_URI,
        REQUEST_PARAMS,
        REQUEST_TIME,
        START_TIME_MILLIS,
        HOST_NAME
    }
}