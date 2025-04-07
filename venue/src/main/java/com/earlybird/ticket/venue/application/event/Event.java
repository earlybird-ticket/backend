package com.earlybird.ticket.venue.application.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event<T extends EventPayload> {
    private EventType eventType;
    private T payload;
    private String timestamp;

    public static Event<? extends EventPayload> fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Event<? extends EventPayload>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }
    }
}
