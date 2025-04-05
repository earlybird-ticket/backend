package com.earlybird.ticket.common;

import com.earlybirdticket.common.configuration.JpaConfig;
import com.earlybirdticket.common.configuration.QueryDslConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JpaConfig.class, QueryDslConfig.class})
public class CommonConfig {

}
