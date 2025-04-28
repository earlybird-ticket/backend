package com.earlybird.ticket.reservation.infrastructure.repository.reservation;

import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationRepository;
    private final ReservationQueryDslRepository reservationQueryDslRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(UUID reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Override
    public List<Reservation> findAllById(List<UUID> reservationIds) {
        return reservationRepository.findAllById(reservationIds);
    }

    @Override
    public Optional<Reservation> findByIdAndReservationStatusConfirmed(UUID reservationId) {
        return reservationRepository.findByIdAndReservationStatusConfirmed(reservationId);
    }
}
