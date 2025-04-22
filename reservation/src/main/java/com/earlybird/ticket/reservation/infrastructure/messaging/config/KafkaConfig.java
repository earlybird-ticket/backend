package com.earlybird.ticket.reservation.infrastructure.messaging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic reservationToSeatForPreemption() {
        return new NewTopic("ReservationToSeatForPreemption",
                            3,
                            (short) 3);
    }

    @Bean
    public NewTopic reservationToSeat() {
        return new NewTopic("ReservationToSeat",
                            3,
                            (short) 3);
    }

    @Bean
    public NewTopic paymentToReservation() {
        return new NewTopic("PaymentToReservation",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic reservationToPayment() {
        return new NewTopic("ReservationToPayment",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic reservationToCoupon() {
        return new NewTopic("ReservationToCoupon",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic seatToReservationForPreemptionDlt() {
        return new NewTopic("SeatToReservationForPreemption.DLT",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic reservationToCouponDlt() {
        return new NewTopic("ReservationToCoupon.DLT",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic paymentToReservationDlt() {
        return new NewTopic("PaymentToReservation.DLT",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic createReservationDlt() {
        return new NewTopic("CreateReservation.DLT",
                            1,
                            (short) 3);
    }

    @Bean
    public NewTopic seatToReservation() {
        return new NewTopic("SeatToReservation",
                            3,
                            (short) 3);
    }

    @Bean
    public NewTopic testTopic() {
        return new NewTopic("TestTopic",
                            1,
                            (short) 3);
    }


}
