package com.earlybird.ticket.reservation.domain.entity;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Event<T extends EventPayload> {
    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("payload")
    private T payload;

    @JsonProperty("timestamp")
    private String timestamp;

    @Builder
    public Event(EventType eventType,
                 T payload,
                 String timestamp) {
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
    }


}
