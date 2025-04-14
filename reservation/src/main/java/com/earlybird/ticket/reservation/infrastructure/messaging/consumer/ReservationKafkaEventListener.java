package com.earlybird.ticket.reservation.infrastructure.messaging.consumer;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dispatcher.EventDispatcher;
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
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = {SEAT_RESERVE_TOPIC, RESERVATION_TO_COUPON, PAYMENT_TO_RESERVATION}, containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload String message,
                       Acknowledgment ack) {
        try {
            log.info("message = {} ",
                     message);
            //message 역직렬화
            Event<? extends EventPayload> event = Event.fromJson(message);
            eventDispatcher.handle(event);
            ack.acknowledge();
        } catch (Exception e) {
            //TODO:: Retry 필요
            log.error("메시지 처리 실패: {}",
                      e.getMessage());
        }
    }

}
