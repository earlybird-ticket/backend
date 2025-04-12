package com.earlybird.ticket.reservation.infrastructure.messaging.consumer;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dispatcher.EventDispatcher;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaEventListener {
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = {EventType.Topic.SEAT_RESERVE_TOPIC}, containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload String message,
                       Acknowledgment ack) {
        try {
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
