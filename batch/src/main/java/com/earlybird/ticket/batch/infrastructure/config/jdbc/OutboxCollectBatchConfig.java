package com.earlybird.ticket.batch.infrastructure.config.jdbc;

import com.earlybird.ticket.batch.domain.entity.Outbox;
import com.earlybird.ticket.batch.infrastructure.dto.response.OutboxMessage;
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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class OutboxCollectBatchConfig {

    public static final int CHUNK_SIZE = 1000;
    public static final String OUTBOX_COLLECT_JOB = "OUT_BOX_COLLECT_JOB";

    @Bean
    public ItemProcessor<OutboxMessage, Outbox> sinkOutboxProcessor() {
        return outboxMessage -> Outbox.withoutId()
            .orgId(outboxMessage.id())
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

    /*
     * TODO : admin, concert, coupon, reservation, venue 추가
     *  1. ItemReader 빈 추가
     *  2. QueryProvider 추가
     *  3. ItemWriter 빈 추가
     *  4. 각 서비스 step으로 빈 추가
     *  5. Job에 각 서비스 Step 연결
     */
    @Bean
    public Job collectOutboxJob(Step collectPaymentOutboxStep, JobRepository jobRepository) {
        // TODO : step으로 admin, concert, coupon, reservation, venue 추가
        return new JobBuilder(OUTBOX_COLLECT_JOB, jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(collectPaymentOutboxStep)
            .build();
    }
}

