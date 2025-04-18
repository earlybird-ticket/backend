package com.earlybird.ticket.payment.common;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.domain.entity.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class EventConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T extends EventPayload> String serializeEvent(Event<T> event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
