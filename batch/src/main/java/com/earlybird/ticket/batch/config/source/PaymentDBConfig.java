package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PaymentDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-payment")
    public DataSourceProperties paymentDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource paymentDataSource() {
        return paymentDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }


    @Bean
    public PlatformTransactionManager paymentTransactionManager() {
        return new DataSourceTransactionManager(paymentDataSource());
    }

}
