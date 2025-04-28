package com.earlybird.ticket.coupon.infrastructure.message;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventDispatcher;
import com.earlybird.ticket.coupon.application.event.EventType.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Profile("!test")
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaEventListener {

    private final EventDispatcher eventDispatcher;

    @KafkaListener(
            topics = {
                    Topic.ADMIN_TO_COUPON_TOPIC
            },
            containerFactory = "kafkaListenerContainerFactory", groupId = "test-group-id"
    )
    public void listen(@Payload String message, Acknowledgment ack) {
        try {
            Event<EventPayload> event = Event.fromJson(message);
            if (event != null) {
                log.info("payload={}", event.getPayload());
                eventDispatcher.handle(event);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            // 재처리 로직
        }
    }
}
