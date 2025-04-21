package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class VenueDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-venue")
    public DataSourceProperties venueDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource venueDataSource() {
        return venueDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public PlatformTransactionManager venueTransactionManager() {
        return new DataSourceTransactionManager(venueDataSource());
    }
}
