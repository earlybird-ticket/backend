package com.earlybird.ticket.reservation.infrastructure.repository.reservationSeat;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import com.earlybird.ticket.reservation.infrastructure.repository.reservation.ReservationQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationSeatRepositoryImpl implements ReservationSeatRepository {
    private final ReservationSeatJpaRepository reservationSeatJpaRepository;
    private final ReservationQueryDslRepository reservationQueryDslRepository;


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
    public List<ReservationSeat> saveAll(List<ReservationSeat> reservationSeatList) {
        return reservationSeatJpaRepository.saveAll(reservationSeatList);
    }

    @Override
    public List<ReservationSeat> findByReservation(Reservation reservation) {
        return reservationSeatJpaRepository.findByReservation(reservation);
    }

    @Override
    public Page<ReservationSearchResult> searchReservations(String q,
                                                            String startTime,
                                                            String endTime,
                                                            Pageable pageable,
                                                            PassportDto passportDto) {
        return reservationQueryDslRepository.searchReservations(q,
                                                                startTime,
                                                                endTime,
                                                                pageable,
                                                                passportDto);
    }


}
