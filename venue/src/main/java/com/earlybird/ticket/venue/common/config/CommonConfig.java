package com.earlybird.ticket.venue.common.config;

import com.earlybird.ticket.common.configuration.JpaConfig;
import com.earlybird.ticket.common.configuration.QueryDslConfig;
import com.earlybird.ticket.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        JpaConfig.class,
        QueryDslConfig.class,
        GlobalExceptionHandler.class
})
@ComponentScan(basePackages = "com.earlybird.ticket.common.aop")
public class CommonConfig {

}
