package com.earlybird.ticket.reservation.domain.repository;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ReservationSeatRepository {

    List<ReservationSeat> findAllBySeatInstaceIdIn(List<UUID> seatInstanceList);

    boolean existsBySeatInstanceIdAndSeatStatusNotFREE(@NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
                                                       SeatStatus free);

    List<ReservationSeat> saveAll(List<ReservationSeat> reservationSeatList);

    List<ReservationSeat> findByReservation(Reservation reservation);

    Page<ReservationSearchResult> searchReservations(String q,
                                                     String startTime,
                                                     String endTime,
                                                     Pageable pageable,
                                                     PassportDto passportDto);

    List<ReservationSeat> findAllByInstanceIdIn(List<UUID> uuids);
}
