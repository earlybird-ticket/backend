package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ReservationDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-reservation")
    public DataSourceProperties reservationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource reservationDataSource() {
        return reservationDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public PlatformTransactionManager reservationTransactionManager() {
        return new DataSourceTransactionManager(reservationDataSource());
    }
}
