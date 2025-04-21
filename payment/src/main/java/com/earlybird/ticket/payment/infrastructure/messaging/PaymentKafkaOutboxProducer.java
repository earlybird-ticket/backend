package com.earlybird.ticket.payment.infrastructure.messaging;

import com.earlybird.ticket.payment.domain.entity.Outbox;
import com.earlybird.ticket.payment.domain.repository.OutboxRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaOutboxProducer {

    private static final String DLT_SUFFIX = ".DLT";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> paymentKafkaTemplate;

    @Scheduled(fixedDelay = 7000)
    @Transactional
    public void publishOutbox() {
        List<Outbox> outboxes = outboxRepository.findTop100UnmarkedOutboxOrderByCreatedAt();

        List<CompletableFuture<Outbox>> futures = outboxes.stream()
            .map(outbox -> {
                log.info("Outbox 발행] id = {}, payload = {}", outbox.getId(), outbox.getPayload());
                // 실패 테스트
//                CompletableFuture<Object> failed = new CompletableFuture<>();
//                failed.completeExceptionally(new RuntimeException("테스트"));
//                return failed
                return paymentKafkaTemplate.send(
                        outbox.getEventType().getTopic(),
                        outbox.getPayload()
                    )
                    .handle((res, ex) -> {
                        if (ex != null) {
                            log.error("Outbox 발행 실패] id =  {}, retryCount = {}",
                                outbox.getId(),
                                outbox.getRetryCount()
                            );
                            // 카운트 올림
                            outbox.incrementRetry();

                            if (outbox.getRetryCount() > 3) {
                                log.error("이벤트 3회 발행 실패 => DLQ로 이동 = {}", outbox.getId());
                                paymentKafkaTemplate.send(
                                    outbox.getEventType().getTopic() + DLT_SUFFIX,
                                    outbox.getPayload()
                                );
                            }
                            return outbox;
                        }
                        outbox.markSuccess();
                        return outbox;
                    });

            }).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            // 모든 작업들이 끝날 때까지 대기 후 한 번만 실행
            .thenRun(() -> {
                List<Outbox> updated = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

                outboxRepository.saveAll(updated);
            })
            .exceptionally(ex -> {
                log.error("Outbox 저장 중 에러 발생", ex);
                return null;
            });
    }
}

