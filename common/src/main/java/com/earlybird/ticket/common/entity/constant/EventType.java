package com.earlybird.ticket.common.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    // TODO : Kafka 적용 이후 Event Type 생성
//    CONSUMER_DELIVERY_MANAGER_ERROR(AssignFailEventPayload.class, Topic.MANAGER_ERROR);
//
//    private final Class<? extends EventPayload> payloadClass;
//    private final String topic;
//
//    public static EventType from(String type) {
//        try {
//            return valueOf(type);
//        } catch (Exception e) {
//            log.error("[EventType.from] type={} is invalid", type, e);
//            return null;
//        }
//    }
//
//    public static class Topic {
//
//        public static final String MANAGER_ERROR = "manager.error";
//    }
}