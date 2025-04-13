package com.earlybird.ticket.reservation.domain.repository;

import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationSeatRepository {
    ReservationSeat save(ReservationSeat reservationSeat);

    List<ReservationSeat> findAllBySeatInstaceIdIn(List<UUID> seatInstanceList);

    boolean existsBySeatInstanceIdAndSeatStatusNotFREE(@NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
                                                       SeatStatus free);

    Optional<ReservationSeat> findById(UUID uuid);

    List<ReservationSeat> saveAll(List<ReservationSeat> reservationSeatList);
}
