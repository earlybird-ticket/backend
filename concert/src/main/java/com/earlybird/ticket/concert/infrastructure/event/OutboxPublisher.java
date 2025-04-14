package com.earlybird.ticket.concert.infrastructure.event;

import com.earlybird.ticket.concert.common.Outbox;
import com.earlybird.ticket.concert.common.OutboxRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "publishPendingEventExecutor"
    )
    @Transactional
    public void publishPendingEvent() {
        List<Outbox> outboxes = outboxRepository
                .findAllByCreatedAtLessThanEqualAndSuccessFalseOrderByCreatedAtAsc(
                        LocalDateTime.now().minusSeconds(10),
                        Pageable.ofSize(100)
                );
        for (Outbox outbox : outboxes) {
            outbox.incrementRetry();
            publishEvent(outbox);
        }
    }

    public void publishEvent(Outbox outbox) {
        try {
            kafkaTemplate.send(
                    outbox.getEventType().getTopic(),
                    outbox.getPayload()
            ).get(1, TimeUnit.SECONDS);
            outbox.markSuccess();
        } catch (Exception e) {
            // TODO : 재시도 로직 구현 예정 & 일정 Try를 넘기면 DLQ로 전환
            log.error("outbox={}", outbox, e);
        }
    }

}
