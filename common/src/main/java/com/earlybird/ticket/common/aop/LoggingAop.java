package com.earlybird.ticket.common.aop;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAop {

    private final LoggingTracer loggingTracer;

    @Pointcut("execution(* com.earlybird..presentation..*(..)) || execution(* com.earlybird..application..*(..))")
    public void loggableComponents() {
    }

    @Around("loggableComponents()")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature()
                                    .toShortString();
        Object[] args = joinPoint.getArgs();
        loggingTracer.methodCall(signature,
                                 args);

        try {
            Object result = joinPoint.proceed();

            HttpServletResponse response = getResponseSafely();
            loggingTracer.methodReturn(signature,
                                       response);

            return result;
        } catch (Throwable throwable) {
            loggingTracer.throwException(signature,
                                         throwable);
            throw throwable;
        }
    }

    private HttpServletResponse getResponseSafely() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getResponse();
        }
        return null; // fallback: 없으면 null 넘긴다 (LoggingTracer가 200 기본값 처리)
    }
}