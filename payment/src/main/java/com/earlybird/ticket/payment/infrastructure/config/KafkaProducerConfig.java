package com.earlybird.ticket.payment.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    private final String BOOTSTRAP_SERVERS;

    public KafkaProducerConfig(
        @Value("${spring.kafka.bootstrap-servers}") String BOOTSTRAP_SERVERS
    ) {
        this.BOOTSTRAP_SERVERS = BOOTSTRAP_SERVERS;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Kafka 서버 주소 설정
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // Key와 Value 직렬화 설정 (Key는 String, Value는 JSON)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 메시지 중복 방지 (Idempotence 활성화 true면 멱등성 활성화)
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // 모든 리더(브로커)가 메시지를 받았을 때만 확인 응답 (최대 신뢰성) 내 ppt에 설명있음!
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // 실패 시 재시도 횟수 (최대 3번)
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // 동시 요청 개수 제한 (Idempotence 사용 시 5 이하 권장)
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // 배치 크기 (메시지를 일정 크기로 모아서 전송)
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        // 메시지 전송 전 대기 시간 (1ms 대기 후 전송)
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        // 프로듀서가 버퍼링할 수 있는 메모리 크기 (32MB)
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        // 메시지 압축 유형 설정 (gzip 사용)
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
        ProducerInterceptor<String, String> producerInterceptor,
        ProducerListener<String, String> producerListener
    ) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setProducerInterceptor(producerInterceptor);
        kafkaTemplate.setProducerListener(producerListener);
        return kafkaTemplate;
    }
}
