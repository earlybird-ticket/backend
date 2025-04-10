package com.earlybird.ticket.admin.infrastructure.event;

import com.earlybird.ticket.admin.common.Outbox;
import com.earlybird.ticket.admin.common.OutboxRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(Outbox outbox) {
        log.info("Outbox : {}", outbox);
        outboxRepository.save(outbox);
    }

    @Async("publishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOutboxAfterCommit(Outbox outbox) {
        publishEvent(outbox);
    }

    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "publishPendingEventExecutor"
    )
    public void publishPendingEvent() {
        List<Outbox> outboxes = outboxRepository
                .findAllByCreatedAtLessThanEqualAndSuccessFalseOrderByCreatedAtAsc(
                        LocalDateTime.now().minusSeconds(10),
                        Pageable.ofSize(100)
                );
        for (Outbox outbox : outboxes) {
            publishEvent(outbox);
        }
    }

    private void publishEvent(Outbox outbox) {
        try {
            kafkaTemplate.send(
                    outbox.getEventType().getTopic(),
                    outbox.getPayload()
            ).get(1, TimeUnit.SECONDS);
            outboxRepository.delete(outbox);
        } catch (Exception e) {
            log.error("outbox={}", outbox, e);
        }
    }

}
