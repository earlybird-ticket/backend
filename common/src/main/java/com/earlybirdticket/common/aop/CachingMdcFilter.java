package com.earlybirdticket.common.aop;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.MDC;

public class CachingMdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        setMdc(httpRequest);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void setMdc(HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        MDC.put(MdcKey.REQUEST_ID.name(), UUID.randomUUID().toString());
        MDC.put(MdcKey.REQUEST_IP.name(), request.getRemoteAddr());
        MDC.put(MdcKey.REQUEST_URI.name(), request.getRequestURI());
        MDC.put(MdcKey.REQUEST_METHOD.name(), request.getMethod());
        MDC.put(MdcKey.REQUEST_TIME.name(), LocalDateTime.now().toString());
        MDC.put(MdcKey.START_TIME_MILLIS.name(), String.valueOf(System.currentTimeMillis()));
    }


    public enum MdcKey {
        REQUEST_ID, REQUEST_IP, REQUEST_METHOD, REQUEST_URI, REQUEST_PARAMS, REQUEST_TIME, START_TIME_MILLIS
    }


}