package com.earlybird.ticket.payment.common;

import com.earlybird.ticket.common.configuration.JpaConfig;
import com.earlybird.ticket.common.configuration.QueryDslConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.earlybird.ticket.common")
public class CommonConfig {

}
