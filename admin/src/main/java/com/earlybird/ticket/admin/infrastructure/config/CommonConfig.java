package com.earlybird.ticket.admin.infrastructure.config;

import com.earlybird.ticket.common.configuration.JpaConfig;
import com.earlybird.ticket.common.configuration.QueryDslConfig;
import com.earlybird.ticket.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        QueryDslConfig.class,
        GlobalExceptionHandler.class,
        JpaConfig.class
})
public class CommonConfig {

}
