package com.earlybird.ticket.batch.infrastructure.config.jdbc;

import static com.earlybird.ticket.batch.infrastructure.config.jdbc.OutboxCollectBatchConfig.CHUNK_SIZE;

import com.earlybird.ticket.batch.domain.entity.Outbox;
import com.earlybird.ticket.batch.infrastructure.dto.response.OutboxMessage;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
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

@Configuration
@RequiredArgsConstructor
public class CouponStepConfig {

    private final ItemProcessor<OutboxMessage, Outbox> sinkOutboxProcessor;

    @Bean
    @Qualifier("CouponOutboxReader")
    public JdbcPagingItemReader<OutboxMessage> couponOutboxReader(DataSource dataSource)
        throws Exception {
        return new JdbcPagingItemReaderBuilder<OutboxMessage>()
            .name("couponOutboxReader")
            .dataSource(dataSource)
            .fetchSize(CHUNK_SIZE)
            .pageSize(CHUNK_SIZE)
            // 기본 생성자 대신 모든 필드를 지닌 생성자 매핑
            .rowMapper(new DataClassRowMapper<>(OutboxMessage.class))
            .queryProvider(couponQueryProvider(dataSource))
            .build();
    }

    @Bean
    public PagingQueryProvider couponQueryProvider(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("""
            SELECT o.id, o.aggregate_type, o.aggregate_id, o.event_type, o.payload, o.retry_count,
            o.success, o.created_at, o.sent_at
            """);
        queryProvider.setFromClause("FROM p_outbox o");
        queryProvider.setWhereClause(
            "WHERE o.success = true or (o.success = false and o.retry_count >= 3)");
        queryProvider.setSortKeys(Map.of(
            "id", Order.ASCENDING
        ));
        return queryProvider.getObject();
    }

    @Bean
    public CompositeItemWriter<Outbox> couponOutboxMigrateWriter(
        DataSource sinkDataSource,
        DataSource couponDataSource
    ) {

        OutboxItemSqlParameterSourceProvider outboxParameterSource = new OutboxItemSqlParameterSourceProvider();
        // payment -> sink로 복사
        JdbcBatchItemWriter<Outbox> writeToSink = new JdbcBatchItemWriterBuilder<Outbox>()
            .dataSource(sinkDataSource)
            .sql("""
                INSERT INTO p_outbox(id, aggregate_type, aggregate_id, event_type, payload, retry_count, success, created_at, sent_at, org_id)
                VALUES (nextval('p_outbox_seq'), :aggregateType, :aggregateId, :eventType, :payload, :retryCount, :success, :createdAt, :sentAt, :orgId)
                """)
            .itemSqlParameterSourceProvider(outboxParameterSource)
            .build();
        writeToSink.afterPropertiesSet();

        // payment에서 삭제
        JdbcBatchItemWriter<Outbox> deleteCouponOutbox = new JdbcBatchItemWriterBuilder<Outbox>()
            .dataSource(couponDataSource)
            .sql("""
                DELETE FROM p_outbox where id = :orgId
                """)
            .itemSqlParameterSourceProvider(outboxParameterSource)
            .build();
        deleteCouponOutbox.afterPropertiesSet();

        return new CompositeItemWriterBuilder<Outbox>()
            .delegates(writeToSink, deleteCouponOutbox)
            .build();
    }

    /* TODO : TransactionManager 변경
        현재는 sink의 트랜잭션매니저만 가지고 있어 source에서는 롤백 발생 X
     */
    @Bean
    public Step collectCouponOutboxStep(
        JobRepository jobRepository,
        @Qualifier("sinkTransactionManager") PlatformTransactionManager transactionManager,
        @Qualifier("couponDataSource") DataSource couponDataSource,
        @Qualifier("sinkDataSource") DataSource sinkDataSource
    ) throws Exception {
        return new StepBuilder("couponOutboxCollectStep", jobRepository)
            .<OutboxMessage, Outbox>chunk(CHUNK_SIZE, transactionManager)
            .reader(couponOutboxReader(couponDataSource))
            .processor(sinkOutboxProcessor)
            .writer(couponOutboxMigrateWriter(sinkDataSource, couponDataSource))
            .build();
    }
}
