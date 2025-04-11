package com.earlybird.ticket.reservation.domain.repository;

import com.earlybird.ticket.reservation.domain.entity.Reservation;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
}
