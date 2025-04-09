package com.earlybird.ticket.reservation.infrastructure.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KafkaProducerListener implements ProducerListener<String, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord,
                          RecordMetadata recordMetadata) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event",
                    "kafka_success");
        logData.put("topic",
                    recordMetadata.topic());
        logData.put("partition",
                    recordMetadata.partition());
        logData.put("offset",
                    recordMetadata.offset());
        logData.put("key",
                    producerRecord.key());
        logData.put("value",
                    producerRecord.value());
        logData.put("timestamp",
                    System.currentTimeMillis());

        try {
            log.info("Kafka Success Log: {}",
                     objectMapper.writeValueAsString(logData));
        } catch (Exception e) {
            log.error("Failed to serialize success log",
                      e);
        }
    }

    @Override
    public void onError(ProducerRecord<String, String> producerRecord,
                        RecordMetadata recordMetadata,
                        Exception exception) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event",
                    "kafka_error");
        logData.put("topic",
                    recordMetadata != null ? recordMetadata.topic() : "unknown");
        logData.put("partition",
                    recordMetadata != null ? recordMetadata.partition() : "unknown");
        logData.put("offset",
                    recordMetadata != null ? recordMetadata.offset() : "unknown");
        logData.put("key",
                    producerRecord.key());
        logData.put("value",
                    producerRecord.value());
        logData.put("error",
                    exception.getMessage());
        logData.put("timestamp",
                    System.currentTimeMillis());

        try {
            log.error("Kafka Error Log: {}",
                      objectMapper.writeValueAsString(logData));
        } catch (Exception e) {
            log.error("Failed to serialize error log",
                      e);
        }
    }
}