package com.earlybird.ticket.reservation.infrastructure.messaging.consumer;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dispatcher.EventDispatcher;
import com.earlybird.ticket.reservation.application.dispatcher.EventFactory;
import com.earlybird.ticket.reservation.common.exception.RecoverableReservationException;
import com.earlybird.ticket.reservation.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.earlybird.ticket.reservation.domain.entity.constant.EventType.Topic.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaEventListener {

    private final EventFactory eventFactory;
    private final EventDispatcher eventDispatcher;

    @KafkaListener(topics = {TEST_TOPIC, SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC, SEAT_TO_RESERVATION_TOPIC, COUPON_TO_RESERVATION_TOPIC, PAYMENT_TO_RESERVATION_TOPIC, CREATE_RESERVATION_DLT}, containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> record,
                       Acknowledgment ack) {
        try {
            String message = record.value();
            String traceId = Optional.ofNullable(record.headers()
                                                       .lastHeader("traceId"))
                                     .map(header -> new String(header.value(),
                                                               StandardCharsets.UTF_8))
                                     .orElse("default-trace-id");

            MDC.put("traceId",
                    traceId);
            log.info("Received Kafka message = {}, traceId = {}",
                     message,
                     traceId);

            Event<? extends EventPayload> event = eventFactory.createEvent(message);
            log.info("event.getEventType() = {}, class = {}",
                     event.getEventType(),
                     event.getClass());

            eventDispatcher.handle(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패: {}",
                      e.getMessage(),
                      e);
            throw new RecoverableReservationException();
        } finally {
            MDC.remove("traceId");
        }
    }
}