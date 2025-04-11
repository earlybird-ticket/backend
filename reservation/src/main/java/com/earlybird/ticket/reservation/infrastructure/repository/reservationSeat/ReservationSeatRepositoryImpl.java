package com.earlybird.ticket.reservation.infrastructure.repository.reservationSeat;

import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationSeatRepositoryImpl implements ReservationSeatRepository {
    private final ReservationSeatJpaRepository reservationSeatJpaRepository;

    @Override
    public ReservationSeat save(ReservationSeat reservationSeat) {
        return reservationSeatJpaRepository.save(reservationSeat);
    }
}
