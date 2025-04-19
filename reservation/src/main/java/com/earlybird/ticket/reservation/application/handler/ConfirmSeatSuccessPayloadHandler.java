package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatConfirmSuccessPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
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

        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        log.info("Received UUID List: {}",
                 payload.seatInstanceIdList());

        log.info("조회된 seatInstanceList 개수: {}",
                 seatIntanceList.size());
        seatIntanceList.forEach(seat -> {
            log.info("수정 전 상태: {}",
                     seat.getStatus());
            seat.updateStatusConfirmSuccess();
            log.info("수정 후 상태: {}",
                     seat.getStatus());
            seatIntanceList.forEach(ReservationSeat::updateStatusConfirmSuccess);
        });


    }

    @Override
    public boolean support(Event<SeatConfirmSuccessPayload> event) {
        return event.getEventType() == EventType.SEAT_CONFIRM_SUCCESS;
    }
}