package com.earlybird.ticket.batch.infrastructure.config.jdbc;

import com.earlybird.ticket.batch.domain.entity.Outbox;
import com.earlybird.ticket.batch.infrastructure.dto.response.OutboxMessage;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class OutboxCollectBatchConfig {

    private static final int CHUNK_SIZE = 1000;
    private static final String OUTBOX_COLLECT_JOB = "OUT_BOX_COLLECT_JOB";

    @Bean
    @Qualifier("paymentOutboxReader")
    public JdbcPagingItemReader<OutboxMessage> paymentOutboxReader(DataSource dataSource)
        throws Exception {
        JdbcPagingItemReader<OutboxMessage> reader = new JdbcPagingItemReader<>();
        return new JdbcPagingItemReaderBuilder<OutboxMessage>()
            .name("paymentOutboxReader")
            .dataSource(dataSource)
            .fetchSize(CHUNK_SIZE)
            // 기본 생성자 대신 모든 필드를 지닌 생성자 매핑
            .rowMapper(new DataClassRowMapper<>(OutboxMessage.class))
            .queryProvider(paymentQueryProvider(dataSource))
            .build();
    }

    @Bean
    public PagingQueryProvider paymentQueryProvider(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("""
            SELECT o.aggregate_type, o.aggregate_id, o.event_type, o.payload, o.retry_count,
            o.success, o.created_at, o.sent_at
            """);
        queryProvider.setFromClause("FROM p_outbox o");
        queryProvider.setWhereClause(
            "WHERE o.success = true or (o.retry_count >= 3 and o.success = false)");
        queryProvider.setSortKeys(Map.of(
            "created_at", Order.ASCENDING,
            "sent_at", Order.ASCENDING
        ));
        return queryProvider.getObject();
    }

    @Bean
    public ItemProcessor<OutboxMessage, Outbox> paymentOutboxProcessor() {
        return outboxMessage -> Outbox.builder()
            .aggregateType(outboxMessage.aggregateType())
            .aggregateId(outboxMessage.aggregateId())
            .payload(outboxMessage.payload())
            .eventType(outboxMessage.eventType())
            .createdAt(outboxMessage.createdAt())
            .sentAt(outboxMessage.sentAt())
            .success(outboxMessage.success())
            .retryCount(outboxMessage.retryCount())
            .build();
    }


    @Bean
    public JpaItemWriter<Outbox> paymentOutboxJpaWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<Outbox>()
            .entityManagerFactory(emf)
            .build();
    }

    @Bean
    public Step collectPaymentOutboxStep(
        JobRepository jobRepository,
        @Qualifier("sinkTransactionManager") PlatformTransactionManager transactionManager,
        @Qualifier("paymentDataSource") DataSource paymentDataSource,
        @Qualifier("sinkEntityManagerFactory") EntityManagerFactory emf
    ) throws Exception {
        return new StepBuilder("paymentOutboxCollectStep", jobRepository)
            .<OutboxMessage, Outbox>chunk(CHUNK_SIZE, transactionManager)
            .reader(paymentOutboxReader(paymentDataSource))
            .processor(paymentOutboxProcessor())
            .writer(paymentOutboxJpaWriter(emf))
            .build();
    }

    @Bean
    public Job collectOutboxJob(Step collectPaymentOutboxStep, JobRepository jobRepository) {
        return new JobBuilder(OUTBOX_COLLECT_JOB, jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(collectPaymentOutboxStep)
            .build();
    }
}

