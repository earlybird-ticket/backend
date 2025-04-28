package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatReturnSuccessPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReturnSeatSuccessPayloadHandler implements EventHandler<SeatReturnSuccessPayload> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatReturnSuccessPayload> event) {
        SeatReturnSuccessPayload payload = event.getPayload();
        Long userId = payload.passportDto()
                             .getUserId();

        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());

        seatIntanceList.forEach(seat -> seat.updateStatusFREE(userId));

        seatIntanceList.forEach(reservationSeat -> {
            reservationSeat.updateStatusFREE(userId);
            Reservation reservation = reservationSeat.getReservation();
            reservation.updateStatusCancelled(userId);
        });


    }

    @Override
    public boolean support(Event<SeatReturnSuccessPayload> event) {
        return event.getEventType() == EventType.SEAT_RETURN_SUCCESS;
    }
}