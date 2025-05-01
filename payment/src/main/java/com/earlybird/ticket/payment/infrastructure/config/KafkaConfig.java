package com.earlybird.ticket.payment.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic seatToReservation() {
        return new NewTopic("paymentToReservation",
                            10,
                            (short) 3);
    }

    @Bean
    public NewTopic seatToReservationDLT() {
        return new NewTopic("paymentToReservation.DLT",
                            3,
                            (short) 3);
    }
}
