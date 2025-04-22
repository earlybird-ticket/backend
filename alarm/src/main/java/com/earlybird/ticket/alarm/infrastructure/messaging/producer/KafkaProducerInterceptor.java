package com.earlybird.ticket.alarm.infrastructure.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KafkaProducerInterceptor implements ProducerInterceptor<String, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> producerRecord) {
        log.info("onSend start");

        Map<String, Object> logData = new HashMap<>();
        logData.put("event",
                    "kafka_send");
        logData.put("topic",
                    producerRecord.topic());
        logData.put("key",
                    producerRecord.key());
        logData.put("value",
                    producerRecord.value());
        logData.put("timestamp",
                    System.currentTimeMillis());

        try {
            log.info("Kafka Interceptor Log: {}",
                     objectMapper.writeValueAsString(logData));
        } catch (Exception e) {
            log.error("Failed to serialize Kafka log data",
                      e);
        }

        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata,
                                  Exception exception) {
        log.info("onAcknowledgement start");

        Map<String, Object> logData = new HashMap<>();
        logData.put("event",
                    "kafka_ack");
        logData.put("topic",
                    metadata.topic());
        logData.put("partition",
                    metadata.partition());
        logData.put("offset",
                    metadata.offset());
        logData.put("timestamp",
                    System.currentTimeMillis());

        if (exception != null) {
            logData.put("status",
                        "failed");
            logData.put("error",
                        exception.getMessage());
            log.error("Kafka Acknowledgement Error: {}",
                      exception.getMessage());
        } else {
            logData.put("status",
                        "success");
            log.info("Kafka Acknowledgement Log: {}",
                     logData);
        }
    }

    @Override
    public void close() {
        log.info("Kafka ProducerInterceptor closed.");
    }

    @Override
    public void configure(Map<String, ?> configs) {
        log.info("Kafka ProducerInterceptor configured.");
    }
}