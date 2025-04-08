package com.earlybird.ticket.reservation.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic ReservationToSeatForPreemption() {
        return new NewTopic("ReservationToSeatForPreemption",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic ReservationToSeat() {
        return new NewTopic("ReservationToSeat",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic ReservationToPayment() {
        return new NewTopic("ReservationToPayment",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic ReservationToCoupon() {
        return new NewTopic("ReservationToCoupon",
                            1,
                            (short) 3);
    }


}
