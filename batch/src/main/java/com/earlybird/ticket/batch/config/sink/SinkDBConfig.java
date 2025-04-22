package com.earlybird.ticket.batch.config.sink;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
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
@RequiredArgsConstructor
public class SinkDBConfig {

    // Spring이 제공하는 기본 JPA 설정 읽어옴
    private final JpaProperties jpaProperties;

    private final HibernateProperties hibernateProperties;

    @Bean
    @ConfigurationProperties("spring.datasource-sink")
    public DataSourceProperties sinkDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource sinkDataSource() {
        return sinkDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }


    @Bean(name = "sinkEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sinkEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        // spring.jpa.* 설정값 가져옴.
        Map<String, String> properties = jpaProperties.getProperties();

        // Hibernate가 제공하는 기본 SpringPhysicalNamingStrategy 사용(CamelCase -> snake_case) vice versa
        Map<String, Object> finalProps = hibernateProperties.determineHibernateProperties(
            properties, new HibernateSettings());

        em.setDataSource(sinkDataSource());
        em.setPackagesToScan("com.earlybird.ticket.batch.domain.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(finalProps);
        return em;
    }

    @Bean
    public PlatformTransactionManager sinkTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(sinkEntityManagerFactory().getObject());
        return transactionManager;
    }

}
