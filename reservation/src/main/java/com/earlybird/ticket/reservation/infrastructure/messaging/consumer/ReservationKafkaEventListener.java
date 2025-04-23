package com.earlybird.ticket.reservation.infrastructure.messaging.consumer;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dispatcher.EventDispatcher;
import com.earlybird.ticket.reservation.application.dispatcher.EventFactory;
import com.earlybird.ticket.reservation.common.exception.RecoverableReservationException;
import com.earlybird.ticket.reservation.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.earlybird.ticket.reservation.domain.entity.constant.EventType.Topic.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaEventListener {

    private final EventFactory eventFactory;
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = {TEST_TOPIC, SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC, SEAT_TO_RESERVATION_TOPIC, COUPON_TO_RESERVATION_TOPIC, PAYMENT_TO_RESERVATION_TOPIC, RESERVATION_TO_COUPON_TOPIC, CREATE_RESERVATION_DLT}, containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload String message,
                       Acknowledgment ack) {
        try {
            log.info("Received Kafka message = {}",
                     message);

            Event<? extends EventPayload> event = eventFactory.createEvent(message);
            log.info("event.getEventType() = {}, class = {}",
                     event.getEventType(),
                     event.getClass());
            eventDispatcher.handle(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패: {}",
                      e.getMessage(),
                      e);
            throw new RecoverableReservationException();
        }
    }
}