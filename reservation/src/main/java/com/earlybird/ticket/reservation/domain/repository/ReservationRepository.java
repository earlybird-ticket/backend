package com.earlybird.ticket.reservation.domain.repository;

import com.earlybird.ticket.reservation.domain.entity.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Optional<Reservation> findById(UUID reservationId);

    List<Reservation> findAllById(List<UUID> reservationIds);

    Optional<Reservation> findByIdAndReservationStatusConfirmed(UUID reservationId);
}
