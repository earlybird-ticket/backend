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
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaOutboxProducer {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingOutboxMessages() {
        List<Outbox> pendingOutboxes = outboxRepository.findTOP100ByOrderByCreatedAtAsc();

        for (Outbox outbox : pendingOutboxes) {
            try {
                log.info("outbox topic = {}",
                         outbox.getEventType()
                               .getTopic());
                log.info("outbox eventType = {}",
                         outbox.getEventType());
                // topic 결정
                String topic = Objects.requireNonNull(outbox.getEventType())
                                      .getTopic();

                // 발행
                kafkaTemplate.send(topic,
                                   outbox.getAggregateId()
                                         .toString(),
                                   outbox.getPayload())
                             .get();

                // 성공 처리
                outbox.markSuccess();

            } catch (Exception e) {
                log.error("Failed to publish outbox message: {}",
                          outbox.getId(),
                          e);
                outbox.incrementRetry();
                outboxRepository.save(outbox);
            }
        }
    }
}