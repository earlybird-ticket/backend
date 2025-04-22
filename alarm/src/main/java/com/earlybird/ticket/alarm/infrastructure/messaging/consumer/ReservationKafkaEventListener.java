package com.earlybird.ticket.alarm.infrastructure.messaging.consumer;

import com.earlybird.ticket.alarm.application.dispatcher.EventDispatcher;
import com.earlybird.ticket.alarm.common.exception.RecoverableReservationException;
import com.earlybird.ticket.alarm.domain.Event;
import com.earlybird.ticket.common.entity.EventPayload;
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

    @KafkaListener(topics = {}, containerFactory = "kafkaListenerContainerFactory")
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
            log.error("메시지 처리 실패: {}",
                      e.getMessage());
            throw new RecoverableReservationException();
        }
    }

}
