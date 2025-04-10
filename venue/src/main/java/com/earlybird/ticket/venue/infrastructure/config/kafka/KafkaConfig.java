package com.earlybird.ticket.venue.infrastructure.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * 좌석 반환 / 확정에 대한 성공 / 실패 응답
     * 실시간성, 빈도수에 따라 하나의 토픽으로 사용
     */
    @Bean
    public NewTopic seatToReservation() {
        return new NewTopic("seatToReservation", 1, (short) 3);
    }

    /**
     * 좌석 선점 성공 / 실패 응답
     * 실시간성, 빈도수에 따라 해당 이벤트용 토픽 분리
     */
    @Bean
    public NewTopic seatToReservationForPreemption() {
        return new NewTopic("seatToReservationForPreemption", 1, (short) 3);
    }
}
