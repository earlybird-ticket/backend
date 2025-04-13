package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptSuccessPayload;
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
public class ReserveSeatSuccessPayloadHandler implements EventHandler<SeatPreemptSuccessPayload> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatPreemptSuccessPayload> event) {
        SeatPreemptSuccessPayload payload = event.getPayload();
        log.info("event 수행 !!!!!!!!!");
        log.info("payload = {} ",
                 payload);

        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        log.info("Received UUID List: {}",
                 payload.seatInstanceIdList());

        log.info("조회된 seatInstanceList 개수: {}",
                 seatIntanceList.size());
        seatIntanceList.forEach(seat -> {
            log.info("수정 전 상태: {}",
                     seat.getStatus());
            seat.updateStatusReserveSuccess();
            log.info("수정 후 상태: {}",
                     seat.getStatus());
        });

        seatIntanceList.forEach(ReservationSeat::updateStatusReserveSuccess);

        seatIntanceList.forEach(seat -> log.info("업데이트 후 상태 확인: id={}, status={}",
                                                 seat.getSeatInstanceId(),
                                                 seat.getStatus()));

    }

    @Override
    public boolean support(Event<SeatPreemptSuccessPayload> event) {
        return event.getEventType() == EventType.SEAT_PREEMPT_SUCCESS;
    }
}