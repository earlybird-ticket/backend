package com.earlybird.ticket.reservation.infrastructure.messaging.producer;

import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaOutboxProducer {

    private static final String DLT_SUFFIX = ".DLT";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 7000)
    @Transactional
    public void publishPendingOutboxMessages() {
        List<Outbox> outboxes = outboxRepository.findTOP100ByOrderByCreatedAtAsc();

        for (Outbox outbox : outboxes) {
            try {
                log.info("Publishing event, id={}",
                         outbox.getId());
                log.info("Event topic: {}, Event payload: {}",
                         outbox.getEventType()
                               .getTopic(),
                         outbox.getPayload());

                kafkaTemplate.send(outbox.getEventType()
                                         .getTopic(),
                                   outbox.getPayload());
                outbox.markSuccess();
            } catch (Exception e) {
                log.warn("Failed to publish event, id={}, retryCount={}",
                         outbox.getId(),
                         outbox.getRetryCount(),
                         e);
                outbox.incrementRetry();

                if (outbox.getRetryCount() >= 3) {
                    kafkaTemplate.send(outbox.getEventType()
                                             .getTopic() + ".DLT",
                                       outbox.getPayload());
                    log.error("Event sent to DLQ");
                }
            }
        }
    }
}