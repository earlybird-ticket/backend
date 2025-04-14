package com.earlybird.ticket.payment.infrastructure.messaging;

import com.earlybird.ticket.payment.domain.entity.Outbox;
import com.earlybird.ticket.payment.domain.repository.OutboxRepository;
import java.util.List;
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

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutbox() {
        List<Outbox> outboxes = outboxRepository.findTop100UnmarkedOutboxOrderByCreatedAt();

        for (Outbox outbox : outboxes) {
            try {
                log.info("Outbox 발행] id = {}, payload = {}", outbox.getId(), outbox.getPayload());
                paymentKafkaTemplate.send(outbox.getEventType().getTopic(), outbox.getPayload());
                outbox.markSuccess();

            } catch (Exception e) {
                log.error("Outbox 발행 실패] id =  {}, retryCount = {}",
                    outbox.getId(),
                    outbox.getRetryCount()
                );
                // 실패 시 카운트 올림
                outbox.incrementRetry();

                if (outbox.getRetryCount() < 3) {
                    continue;
                }
                // 실패횟수가 3회 이상이면 =>
                log.error("이벤트 3회 발행 실패 => DLQ로 이동");
                paymentKafkaTemplate.send(
                    outbox.getEventType().getTopic() + DLT_SUFFIX,
                    outbox.getPayload()
                );
            }
        }
    }
}
