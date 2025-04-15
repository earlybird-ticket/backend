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
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaOutboxProducer {

    private static final String DLT_SUFFIX = ".DLT";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingOutboxMessages() {
        List<Outbox> pendingOutboxes = outboxRepository.findTOP100ByOrderByCreatedAtAsc();

        List<CompletableFuture<Outbox>> futures = pendingOutboxes.stream()
                                                                 .map(outbox -> {
                                                                     String topic = Objects.requireNonNull(outbox.getEventType())
                                                                                           .getTopic();
                                                                     String key = outbox.getAggregateId()
                                                                                        .toString();
                                                                     String payload = outbox.getPayload();

                                                                     log.info("[Outbox 발행] id = {}, topic = {}, payload = {}",
                                                                              outbox.getId(),
                                                                              topic,
                                                                              payload);

                                                                     return kafkaTemplate.send(topic,
                                                                                               key,
                                                                                               payload)
                                                                                         .handle((res, ex) -> {
                                                                                             if (ex != null) {
                                                                                                 log.error("[발행 실패] id = {}, retryCount = {}",
                                                                                                           outbox.getId(),
                                                                                                           outbox.getRetryCount(),
                                                                                                           ex);
                                                                                                 outbox.incrementRetry();

                                                                                                 if (outbox.getRetryCount() > 3) {
                                                                                                     log.warn("[DLQ 이동] 3회 이상 실패한 이벤트 → {}",
                                                                                                              outbox.getId());
                                                                                                     kafkaTemplate.send(topic + DLT_SUFFIX,
                                                                                                                        key,
                                                                                                                        payload);
                                                                                                 }
                                                                                             } else {
                                                                                                 outbox.markSuccess();
                                                                                             }
                                                                                             return outbox;
                                                                                         });
                                                                 })
                                                                 .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                         .thenRun(() -> {
                             List<Outbox> updatedOutboxes = futures.stream()
                                                                   .map(CompletableFuture::join)
                                                                   .toList();
                             outboxRepository.saveAll(updatedOutboxes);
                         })
                         .exceptionally(ex -> {
                             log.error("[Outbox 저장 중 예외 발생]",
                                       ex);
                             return null;
                         });
    }
}