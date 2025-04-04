package com.earlybirdticket.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.earlybirdticket.common.entity.EventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPayloadConverter {

    private final ObjectMapper objectMapper;

    public String serializePayload(EventPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
// TODO : EventType 정리 후 활성화
//    public EventPayload deserializePayload(String payload, EventType eventType) {
//        try {
//            return objectMapper.readValue(payload, eventType.getPayloadClass());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to deserialize payload", e);
//        }
//    }
}