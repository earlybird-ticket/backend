package com.earlybird.ticket.alarm.common.util;

import com.earlybird.ticket.alarm.domain.Event;
import com.earlybird.ticket.alarm.domain.constant.EventType;
import com.earlybird.ticket.common.entity.EventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventPayloadConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serializePayload(Event payload) {
        try {
            log.info("[CouponEventHandler] 직렬화된 FailCouponPayload: {}",
                     payload);
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("[CouponEventHandler] FailCouponPayload 직렬화 실패",
                      e);
            throw new RuntimeException("Failed to serialize payload",
                                       e);
        }
    }

    public EventPayload deserializePayload(String payload,
                                           EventType eventType) {
        try {
            return objectMapper.readValue(payload,
                                          eventType.getPayloadClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize payload",
                                       e);
        }
    }
}