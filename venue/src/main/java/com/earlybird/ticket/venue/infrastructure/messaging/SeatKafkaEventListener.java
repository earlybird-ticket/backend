package com.earlybird.ticket.venue.infrastructure.messaging;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.application.event.dispatcher.EventDispatcher;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.common.exception.JsonDeserializationException;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatKafkaEventListener {
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = {
            EventType.Topic.ADMIN_TO_SEAT_TOPIC,
            EventType.Topic.CONCERT_TO_SEAT_TOPIC
    }, containerFactory = "kafkaListenerContainerFactory", groupId = "test-group-id")
    public void listenAdminAndConcertToSeatTopic(@Payload String message, Acknowledgment ack) {
        try{
            Event<? extends EventPayload> event = Event.fromJson(message);
            eventDispatcher.handle(event);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            throw e;

        }
    }

    @KafkaListener(topics = {
            EventType.Topic.RESERVATION_TO_SEAT_TOPIC,
    }, containerFactory = "kafkaListenerContainerFactory", groupId = "test-group-id")
    public void listenReservationToSeatTopic(@Payload String message, Acknowledgment ack) {

        try{
            Event<? extends EventPayload> event = Event.fromJson(message);
            eventDispatcher.handle(event);
            ack.acknowledge();

        }  catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            throw e;
        }
    }

}
