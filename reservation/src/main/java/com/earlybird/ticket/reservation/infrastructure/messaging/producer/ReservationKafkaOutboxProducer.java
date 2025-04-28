package com.earlybird.ticket.reservation.infrastructure.messaging.producer;

import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaOutboxProducer {

    private static final String DLT_SUFFIX = ".DLT";
    private static final String TRACE_ID = "traceId";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 7000)
    @Transactional
    public void publishPendingOutboxMessages() {
        List<Outbox> pendingOutboxes = outboxRepository.findTOP100ByOrderByCreatedAtAsc();
        String traceId = MDC.get(TRACE_ID);

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

                                                                     ProducerRecord<String, String> record = new ProducerRecord<>(topic,
                                                                                                                                  key,
                                                                                                                                  payload);
                                                                     if (traceId != null) {
                                                                         record.headers()
                                                                               .add(TRACE_ID,
                                                                                    traceId.getBytes(StandardCharsets.UTF_8));
                                                                     }
                                                                     return kafkaTemplate.send(record)
                                                                                         .handle((res, ex) -> {
                                                                                             if (ex != null) {
                                                                                                 log.error("[Kafka 발행 실패] traceId = {}, id = {}, retryCount = {}",
                                                                                                           traceId,
                                                                                                           outbox.getId(),
                                                                                                           outbox.getRetryCount(),
                                                                                                           ex);
                                                                                                 outbox.incrementRetry();

                                                                                                 if (outbox.getRetryCount() > 3) {
                                                                                                     log.warn("[DLQ 이동] 3회 이상 실패한 이벤트 → {}",
                                                                                                              outbox.getId());
                                                                                                     ProducerRecord<String, String> dltRecord = new ProducerRecord<>(topic + DLT_SUFFIX,
                                                                                                                                                                     key,
                                                                                                                                                                     payload);
                                                                                                     dltRecord.headers()
                                                                                                              .add(TRACE_ID,
                                                                                                                   Objects.requireNonNull(traceId)
                                                                                                                          .getBytes(StandardCharsets.UTF_8));
                                                                                                     kafkaTemplate.send(dltRecord);
                                                                                                 }
                                                                                             } else {
                                                                                                 try {
                                                                                                     MDC.put(TRACE_ID,
                                                                                                             traceId);
                                                                                                     log.info("[Kafka 발행 성공] traceId = {}, offset = {}",
                                                                                                              traceId,
                                                                                                              res.getRecordMetadata()
                                                                                                                 .offset());
                                                                                                     outbox.markSuccess();
                                                                                                 } catch (Exception e) {
                                                                                                     log.error(e.getMessage(),
                                                                                                               e);
                                                                                                 } finally {
                                                                                                     MDC.clear();
                                                                                                 }
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
                             log.error("[Outbox 저장 중 예외] traceId = {}",
                                       traceId,
                                       ex);
                             return null;
                         });
    }
}