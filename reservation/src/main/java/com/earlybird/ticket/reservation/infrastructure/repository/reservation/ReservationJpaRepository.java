package com.earlybird.ticket.reservation.infrastructure.repository.reservation;

import com.earlybird.ticket.reservation.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findById(UUID reservationId);

    Optional<Reservation> findByIdAndReservationStatusConfirmed(UUID reservationId);
}
