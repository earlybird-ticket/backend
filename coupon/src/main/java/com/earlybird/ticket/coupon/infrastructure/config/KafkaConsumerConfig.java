package com.earlybird.ticket.coupon.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@Profile("!test")
@EnableKafka
public class KafkaConsumerConfig {

    Map<String, Object> props = new HashMap<>();
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @ConditionalOnMissingBean
    public ConsumerFactory<String, Object> consumerFactory() {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka 브로커 리스트 설정
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        ); // 메시지 키 역직렬화 방식 설정
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        ); // 메시지 값 역직렬화 방식 설정
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // 신뢰할 수 있는 패키지 설정 (역직렬화 시 필요한 설정)

        // Consumer 그룹 ID 설정 (여러 Consumer가 같은 그룹에서 동일한 메시지를 처리하지 않도록 함)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-id");

        // Offset 자동 커밋 비활성화 (중복 방지, 수동 커밋 사용)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Kafka 컨슈머가 읽을 오프셋 위치 설정 (earliest: 가장 오래된 메시지부터 읽음, 기본값은 latest)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 한 번의 poll() 요청에서 가져올 최대 레코드 수 (성능 조절, 테스트하면서 수를 건드려야 할듯)
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);

        // Poll 간격 최대 시간 (이 시간을 초과하면 Consumer 그룹에서 제거됨)
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 15000);

        // Heartbeat 주기 (Consumer가 정상적으로 살아있음을 브로커에 알림)
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);

        // Consumer가 장애로 인해 응답하지 않을 경우 제거되는 시간 (세션 타임아웃)
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);

        return new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), new JsonDeserializer<>(Object.class));
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            DefaultErrorHandler defaultErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // 메시지 처리 완료 즉시 커밋 (중복 방지)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        // 재시도 처리
        factory.setCommonErrorHandler(defaultErrorHandler);

        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        // DLQ 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    // 원본 토픽 이름에 .DLT 접미사 추가
                    return new TopicPartition(
                            record.topic() + ".DLT",
                            record.partition()
                    );
                }
        );
        // 지터를 포함한 지수 백오프 설정 (최대 5회)
        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(2000); // 2초
        backoff.setMultiplier(2.0);       // 2배씩 증가
        backoff.setMaxInterval(10000);    // 최대 10초

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                backoff
        );

        // 특정 예외는 재시도하지 않고 바로 DLQ로 보내고 싶을 때
        // 이 예외는 재시도 하지 않고 바로 DLQ로 보냄
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class
        );

        return errorHandler;
    }
}
