package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Event<T extends EventPayload> {
    private EventType eventType;
    private T payload;
    private String timestamp;

    public static Event<? extends EventPayload> fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String typeText = root.get("eventType").asText();
            EventType eventType = EventType.from(typeText);
            if (eventType == null) throw new IllegalArgumentException("Unknown event type: " + typeText);

            String timestamp = root.get("timestamp").asText();
            Class<? extends EventPayload> payloadClass = eventType.getPayloadClass();
            EventPayload payload = mapper.treeToValue(root.get("payload"), payloadClass);

            return new Event<>(eventType, payload, timestamp);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }
    }
}
