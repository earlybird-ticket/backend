package com.earlybird.ticket.reservation.presentation;

import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptSuccessPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations/external/test/events")
public class EventTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/seat/success")
    public String publishSeatPreemptSuccess(@RequestBody List<UUID> seatInstanceIdList) {
        // 1. 이벤트 생성
        SeatPreemptSuccessPayload payload = SeatPreemptSuccessPayload.builder()
                                                                     .seatInstanceIdList(seatInstanceIdList)
                                                                     .build();

        Event<SeatPreemptSuccessPayload> event = Event.<SeatPreemptSuccessPayload>builder()
                                                      .eventType(EventType.SEAT_PREEMPT_SUCCESS)
                                                      .payload(payload)
                                                      .timestamp(LocalDateTime.now()
                                                                              .toString())
                                                      .build();

        try {
            // 2. Kafka로 전송
            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(event);
            kafkaTemplate.send(EventType.Topic.RESERVATION_TO_SEAT,
                               json);
            return "메시지 발행 완료";
        } catch (Exception e) {
            return "메시지 발행 실패: " + e.getMessage();
        }
    }
}