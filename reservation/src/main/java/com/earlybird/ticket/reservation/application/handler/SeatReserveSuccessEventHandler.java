package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptSuccessEvent;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatReserveSuccessEventHandler implements EventHandler<SeatPreemptSuccessEvent> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatPreemptSuccessEvent> event) {
        SeatPreemptSuccessEvent payload = event.getPayload();

        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        seatIntanceList.forEach(ReservationSeat::updateStatusReserveSuccess);
    }

    @Override
    public boolean support(Event<SeatPreemptSuccessEvent> event) {
        return event.getEventType() == EventType.INSTANCE_SEAT_RESERVATION;
    }
}