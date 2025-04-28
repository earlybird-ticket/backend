package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatConfirmSuccessPayload;
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
public class ConfirmSeatSuccessPayloadHandler implements EventHandler<SeatConfirmSuccessPayload> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatConfirmSuccessPayload> event) {
        SeatConfirmSuccessPayload payload = event.getPayload();
        Long userId = payload.passportDto()
                             .getUserId();

        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        log.info("Received UUID List: {}",
                 payload.seatInstanceIdList());
        Reservation reservation = seatIntanceList.get(0)
                                                 .getReservation();

        reservation.updateStatusConfirm(userId);
        seatIntanceList.forEach(seat -> seat.updateStatusConfirm(userId));


    }

    @Override
    public boolean support(Event<SeatConfirmSuccessPayload> event) {
        return event.getEventType() == EventType.SEAT_CONFIRM_SUCCESS;
    }
}