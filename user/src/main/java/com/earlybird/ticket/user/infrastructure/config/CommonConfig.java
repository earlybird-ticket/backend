package com.earlybird.ticket.user.infrastructure.config;

import com.earlybird.ticket.common.configuration.JpaConfig;
import com.earlybird.ticket.common.configuration.QueryDslConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({JpaConfig.class, QueryDslConfig.class})
@Configuration
public class CommonConfig {

}
