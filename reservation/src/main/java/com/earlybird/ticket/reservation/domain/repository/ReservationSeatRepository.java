package com.earlybird.ticket.reservation.domain.repository;

import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;

public interface ReservationSeatRepository {
    ReservationSeat save(ReservationSeat reservationSeat);
}
