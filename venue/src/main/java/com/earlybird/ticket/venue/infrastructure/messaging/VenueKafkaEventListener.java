package com.earlybird.ticket.venue.infrastructure.messaging;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.common.event.EventType;
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
            EventType.Topic.ADMIN_TO_VENUE_TOPIC
    }, containerFactory = "kafkaListenerContainerFactory", groupId = "test-group-id")
    public void listenAdminToVenueTopic(@Payload String message, Acknowledgment ack) {
        try{
            Event<? extends EventPayload> event = Event.fromJson(message);
            eventDispatcher.handle(event);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            throw e;
        }
    }

    // TODO : 추후 DLT 처리 로직 추가

}
