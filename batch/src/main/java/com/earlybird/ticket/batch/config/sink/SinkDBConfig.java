package com.earlybird.ticket.batch.config.sink;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.earlybird.ticket.batch.infrastructure.repository",
    entityManagerFactoryRef = "sinkEntityManagerFactory",
    transactionManagerRef = "sinkTransactionManager"
)
public class SinkDBConfig {

    @Bean
    @ConfigurationProperties("spring.datasource-sink")
    public DataSourceProperties sinkDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataDBSource() {
        return sinkDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }


    @Bean(name = "sinkEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sinkEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataDBSource());
        em.setPackagesToScan("com.earlybird.ticket.batch.domain.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public PlatformTransactionManager sinkTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(sinkEntityManagerFactory().getObject());
        return transactionManager;
    }

}
