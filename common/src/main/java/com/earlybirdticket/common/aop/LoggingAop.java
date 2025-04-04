package com.earlybirdticket.common.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAop {

    private final LoggingTracer loggingTracer;

    @Pointcut("execution(* com.earlybirdticket..*Presentation.*(..)) || execution(* com.earlybirdticket..*Application.*(..))")
    public void loggableComponents() {
    }

    @Around("loggableComponents()")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        loggingTracer.methodCall(signature, args); //메서드 호출 - depth +1
        try {
            Object result = joinPoint.proceed();
            loggingTracer.methodReturn(signature); //메서드 리턴 - depth -1;
            return result;
        } catch (Throwable throwable) {
            loggingTracer.throwException(signature, throwable); //예외 발생시 리턴 - depth에 x표시
            throw throwable;
        }
    }

}
