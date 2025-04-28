package com.earlybird.ticket.batch.config.sink;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FlywayConfig {

    @Bean
    public Flyway flywaySink(
        @Qualifier("sinkDataSource") DataSource sinkDataSource
    ) {
        Flyway flyway = Flyway.configure()
            .dataSource(sinkDataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load();

        flyway.migrate();

        return flyway;
    }
}
