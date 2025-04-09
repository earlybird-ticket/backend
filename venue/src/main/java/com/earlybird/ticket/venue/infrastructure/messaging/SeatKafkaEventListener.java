package com.earlybird.ticket.venue.infrastructure.messaging;

import com.earlybird.ticket.venue.application.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatKafkaEventListener {
    private final EventDispatcher eventDispatcher;

    //AdminToSeat

    //ReservationToSeatForPreemption

    //ReservationToSeat

    //concertToSeat

}
