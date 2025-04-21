package com.earlybird.ticket.payment.infrastructure.messaging.consumer;

import com.earlybird.ticket.payment.application.event.dispatcher.EventDispatcher;
import com.earlybird.ticket.payment.application.service.exception.PaymentNonRecoverableException;
import com.earlybird.ticket.payment.common.EventType.Topic;
import com.earlybird.ticket.payment.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaEventListener {

    private final EventDispatcher eventDispatcher;

    @KafkaListener(
        topics = {
            Topic.RESERVATION_TO_PAYMENT_TOPIC,
//            Topic.PAYMENT_TO_PAYMENT_TOPIC
        },
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(@Payload String message, Acknowledgment ack) {
        try {
            log.info("message = {}", message);
            // TODO : EventDispatcher 구현
            eventDispatcher.handle(Event.fromJson(message));
            ack.acknowledge();

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            throw new PaymentNonRecoverableException();
        }
    }
}
