package com.earlybird.ticket.user.infrastructure.config;

import com.earlybird.ticket.common.configuration.JpaConfig;
import com.earlybird.ticket.common.configuration.QueryDslConfig;
import com.earlybird.ticket.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
    JpaConfig.class,
    QueryDslConfig.class,
    GlobalExceptionHandler.class // 공통 예외처리 위해 필요
})
@Configuration
public class CommonConfig {

}
