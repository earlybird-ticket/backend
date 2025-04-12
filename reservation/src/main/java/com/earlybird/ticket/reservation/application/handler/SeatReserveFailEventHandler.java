package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptFailEvent;
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
public class SeatReserveFailEventHandler implements EventHandler<SeatPreemptFailEvent> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatPreemptFailEvent> event) {
        SeatPreemptFailEvent payload = event.getPayload();

        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        seatIntanceList.forEach(reservationSeat -> {
            //예약 상태 CANCEL로 수정
            //TODO:: 예약을 수정할 수 없는 상태이기때문에 삭제처리
            // 수연님에게 passport 추가해서 보내도록 요청하기
            // SeatPreemptFailEvent에 passport 추가후 삭제 처리
            reservationSeat.getReservation()
                           .updateStatusPending();

            //예약 좌석 상태 FREE로 수정
            reservationSeat.updateStatusReserveFail();

            //TODO:: 실패 알람 처리
        });
    }

    @Override
    public boolean support(Event<SeatPreemptFailEvent> event) {
        return event.getEventType() == EventType.SEAT_PREEMPT_SUCCESS;
    }
}