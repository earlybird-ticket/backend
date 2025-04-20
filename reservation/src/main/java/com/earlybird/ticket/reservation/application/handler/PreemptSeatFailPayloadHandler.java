package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptFailPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor

public class PreemptSeatFailPayloadHandler implements EventHandler<SeatPreemptFailPayload> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatPreemptFailPayload> event) {
        SeatPreemptFailPayload payload = event.getPayload();


        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        Long userId = payload.passportDto()
                             .getUserId();
        seatIntanceList.forEach(reservationSeat -> {
            Reservation reservation = reservationSeat.getReservation();
            reservation.updateStatusCancelled(userId);

            //예약 좌석 상태 FREE로 수정
            reservationSeat.updateStatusFREE(userId);

            //TODO:: 실패 알람 처리
            //Code를 가지고 내용 보내기
        });
    }

    @Override

    public boolean support(Event<SeatPreemptFailPayload> event) {
        return event.getEventType() == EventType.SEAT_PREEMPT_FAIL;
    }
}