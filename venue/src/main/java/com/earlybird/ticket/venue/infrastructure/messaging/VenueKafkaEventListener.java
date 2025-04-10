package com.earlybird.ticket.venue.infrastructure.messaging;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.EventType;
import com.earlybird.ticket.venue.application.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VenueKafkaEventListener {
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = {
            EventType.Topic.VENUE_TOPIC
    }, containerFactory = "kafkaListenerContainerFactory", groupId = "test-group-id")
    public void listen(@Payload String message, Acknowledgment ack) {
        try{
            Event<? extends EventPayload> event = Event.fromJson(message);
            eventDispatcher.handle(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            // 재처리 로직
        }
    }

}
