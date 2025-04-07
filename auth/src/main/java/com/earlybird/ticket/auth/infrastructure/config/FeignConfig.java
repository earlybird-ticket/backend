package com.earlybird.ticket.auth.infrastructure.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    Retryer.Default retryer() {
        // 0.1초 간격에서 최대 3초 간격, 최대 5번 재시도
        return new Retryer.Default(100L,
                                   TimeUnit.SECONDS.toMillis(3L),
                                   3);
    }
}
