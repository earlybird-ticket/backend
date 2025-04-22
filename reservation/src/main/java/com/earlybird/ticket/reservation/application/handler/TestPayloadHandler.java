package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.dto.response.TestPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestPayloadHandler implements EventHandler<TestPayload> {

    @Override
    public void handle(Event<TestPayload> event) {
        log.info("[TestPayloadHandler] 처리 시작 - Payload: {}",
                 event.getPayload()
                      .getTest());
    }

    @Override
    public boolean support(Event<TestPayload> event) {
        return event.getEventType() == EventType.TEST_TOPIC;
    }
}