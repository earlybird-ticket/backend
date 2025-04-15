package com.earlybird.ticket.reservation.infrastructure.repository.reservationSeat;

import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationSeatJpaRepository extends JpaRepository<ReservationSeat, UUID> {
    List<ReservationSeat> findAllBySeatInstanceIdIn(List<UUID> seatInstanceList);

    boolean existsBySeatInstanceIdAndStatusNot(UUID seatInstanceId,
                                               SeatStatus free);

    List<ReservationSeat> findByReservation(Reservation reservation);

}
