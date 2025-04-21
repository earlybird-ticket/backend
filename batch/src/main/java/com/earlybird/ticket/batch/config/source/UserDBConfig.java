package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class UserDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-user")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource userDataSource() {
        return userDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public PlatformTransactionManager userTransactionManager() {
        return new DataSourceTransactionManager(userDataSource());
    }
}