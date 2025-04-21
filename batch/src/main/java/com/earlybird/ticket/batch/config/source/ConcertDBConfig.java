package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConcertDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-concert")
    public DataSourceProperties concertDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource concertDataSource() {
        return concertDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public PlatformTransactionManager concertTransactionManager() {
        return new DataSourceTransactionManager(concertDataSource());
    }

}
