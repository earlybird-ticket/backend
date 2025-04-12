package com.earlybird.ticket.reservation.infrastructure.repository.reservationSeat;

import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationSeatRepositoryImpl implements ReservationSeatRepository {
    private final ReservationSeatJpaRepository reservationSeatJpaRepository;

    @Override
    public ReservationSeat save(ReservationSeat reservationSeat) {
        return reservationSeatJpaRepository.save(reservationSeat);
    }

    @Override
    public List<ReservationSeat> findAllBySeatInstaceIdIn(List<UUID> seatInstanceList) {
        return reservationSeatJpaRepository.findAllBySeatInstanceIdIn(seatInstanceList);
    }

    @Override
    public boolean existsBySeatInstanceIdAndSeatStatusNotFREE(UUID seatInstanceId,
                                                              SeatStatus free) {
        return reservationSeatJpaRepository.existsBySeatInstanceIdAndStatusNot(seatInstanceId,
                                                                               free);
    }

    @Override
    public Optional<ReservationSeat> findById(UUID uuid) {
        return reservationSeatJpaRepository.findById(uuid);
    }
}
