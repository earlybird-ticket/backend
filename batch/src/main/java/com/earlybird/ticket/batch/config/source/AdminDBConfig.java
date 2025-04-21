package com.earlybird.ticket.batch.config.source;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AdminDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-admin")
    public DataSourceProperties adminDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource adminDataSource() {
        return adminDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public PlatformTransactionManager adminTransactionManager() {
        return new DataSourceTransactionManager(adminDataSource());
    }

}
