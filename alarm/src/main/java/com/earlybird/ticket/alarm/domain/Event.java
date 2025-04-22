package com.earlybird.ticket.alarm.domain;

import com.earlybird.ticket.alarm.common.exception.CustomJsonProcessingException;
import com.earlybird.ticket.alarm.domain.constant.EventType;
import com.earlybird.ticket.common.entity.EventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
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
            log.info("[fromJson] root: {}",
                     root);

            String typeText = root.get("eventType")
                                  .asText();
            EventType eventType = EventType.from(typeText);
            if (eventType == null)
                throw new IllegalArgumentException("Unknown event type: " + typeText);
            log.info("[fromJson] resolved eventType: {}",
                     eventType);

            String timestamp = root.get("timestamp")
                                   .asText();
            Class<? extends EventPayload> payloadClass = eventType.getPayloadClass();
            log.info("[fromJson] payloadClass: {}",
                     payloadClass.getName());

            EventPayload payload = mapper.treeToValue(root.get("payload"),
                                                      payloadClass);
            return new Event<>(eventType,
                               payload,
                               timestamp);
        } catch (Exception e) {
            log.error("[fromJson] JSON 역직렬화 실패",
                      e);
            throw new CustomJsonProcessingException();
        }
    }
}
