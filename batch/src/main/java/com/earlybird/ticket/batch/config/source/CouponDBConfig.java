package com.earlybird.ticket.batch.config.source;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouponDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-coupon")
    public DataSource couponDataSource() {
        return new AtomikosDataSourceBean();
    }

}
