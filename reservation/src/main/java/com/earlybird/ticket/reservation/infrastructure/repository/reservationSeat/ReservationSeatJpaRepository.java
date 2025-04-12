package com.earlybird.ticket.reservation.infrastructure.repository.reservationSeat;

import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ReservationSeatJpaRepository extends JpaRepository<ReservationSeat, UUID> {
    @Query(value = "SELECT * FROM p_reservation_seat WHERE seat_instance_id IN (:seatInstanceList)", nativeQuery = true)
    List<ReservationSeat> findAllBySeatInstanceIdIn(List<UUID> seatInstanceList);

    boolean existsBySeatInstanceIdAndStatusNot(UUID seatInstanceId,
                                               SeatStatus free);
}
