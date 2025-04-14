package com.earlybird.ticket.venue.common.event.util;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConverter {
    private final ObjectMapper objectMapper;

    public <T extends EventPayload> String serializePayload(Event<T> event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
