package com.earlybird.ticket.reservation.domain.entity;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event<T extends EventPayload> {
    private EventType eventType;
    private T payload;
    private String timestamp;

    @Builder
    public Event(EventType eventType,
                 T payload,
                 String timestamp) {
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public static Event<? extends EventPayload> fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String typeText = root.get("eventType")
                                  .asText();
            EventType eventType = EventType.from(typeText);
            if (eventType == null)
                throw new IllegalArgumentException("Unknown event type: " + typeText);

            String timestamp = root.get("timestamp")
                                   .asText();
            Class<? extends EventPayload> payloadClass = eventType.getPayloadClass();
            EventPayload payload = mapper.treeToValue(root.get("payload"),
                                                      payloadClass);

            return Event.builder()
                        .eventType(eventType)
                        .timestamp(timestamp)
                        .payload(payload)
                        .build();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON",
                                       e);
        }
    }
}
