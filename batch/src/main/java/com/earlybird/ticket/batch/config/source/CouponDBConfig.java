package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CouponDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-coupon")
    public DataSourceProperties couponDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource couponDataSource() {
        return couponDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public PlatformTransactionManager couponTransactionManager() {
        return new DataSourceTransactionManager(couponDataSource());
    }
}
