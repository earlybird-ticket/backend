package com.earlybird.ticket.reservation.application.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventFactory {


    public Event<? extends EventPayload> createEvent(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                   false);
            objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT,
                                   true);
            objectMapper.registerModule(new JavaTimeModule());

            // eventType 추출
            String typeStr = objectMapper.readTree(json)
                                         .get("eventType")
                                         .asText();
            log.info("받아온 데이터 :{}",
                     typeStr);
            EventType eventType = EventType.from(typeStr);
            if (eventType == null) {
                throw new IllegalArgumentException("Unknown eventType: " + typeStr);
            }

            // payload 타입 매핑 및 역직렬화
            Class<? extends EventPayload> payloadType = eventType.getPayloadClass();
            JavaType javaType = objectMapper.getTypeFactory()
                                            .constructParametricType(Event.class,
                                                                     payloadType);
            log.info("type ={}",
                     javaType);
            return objectMapper.readValue(json,
                                          javaType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize Event: " + e.getMessage(),
                                               e);
        }
    }
}