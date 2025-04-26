package com.earlybird.ticket.batch.infrastructure.config.jdbc;

import com.earlybird.ticket.batch.domain.entity.Outbox;
import com.earlybird.ticket.batch.infrastructure.dto.response.OutboxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public Job collectOutboxJob(
        @Qualifier("collectPaymentOutboxStep") Step collectPaymentOutboxStep,
        @Qualifier("collectCouponOutboxStep") Step collectCouponOutboxStep,
        @Qualifier("collectConcertOutboxStep") Step collectConcertOutboxStep,
        @Qualifier("collectVenueOutboxStep") Step collectVenueOutboxStep,
        @Qualifier("collectAdminOutboxStep") Step collectAdminOutboxStep,
        @Qualifier("collectReservationOutboxStep") Step collectReservationOutboxStep,
        JobRepository jobRepository
    ) {
        // 각 step 성공 여부에 관계 없이 job 계속 수행
        return new JobBuilder(OUTBOX_COLLECT_JOB, jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(collectPaymentOutboxStep)
            .on("*")
            .to(collectCouponOutboxStep)
            .from(collectCouponOutboxStep).on("*").to(collectConcertOutboxStep)
            .from(collectConcertOutboxStep).on("*").to(collectVenueOutboxStep)
            .from(collectVenueOutboxStep).on("*").to(collectAdminOutboxStep)
            .from(collectAdminOutboxStep).on("*").to(collectReservationOutboxStep)
            .end()
            .build();
    }
}

