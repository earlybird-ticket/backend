package com.earlybird.ticket.auth.common.config;

import com.earlybird.ticket.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GlobalExceptionHandler.class)
@ComponentScan(basePackages = "com.earlybird.ticket.common.aop")
public class CommonConfig {
}
