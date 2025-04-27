package com.earlybird.ticket.batch.config.source;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-admin")
    public DataSource adminDataSource() {
        return new AtomikosDataSourceBean();
    }

}
