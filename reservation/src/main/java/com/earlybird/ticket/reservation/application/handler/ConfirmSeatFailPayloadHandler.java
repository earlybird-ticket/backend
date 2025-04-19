package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.application.dto.response.SeatConfirmFailPayload;
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

public class ConfirmSeatFailPayloadHandler implements EventHandler<SeatConfirmFailPayload> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatConfirmFailPayload> event) {
        SeatConfirmFailPayload payload = event.getPayload();


        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllByInstanceIdIn(payload.seatInstanceIdList());
        PassportDto passport = payload.passportDto();
        seatIntanceList.forEach(reservationSeat -> {
            reservationSeat.getReservation()
                           .delete(passport.getUserId());

            //예약 좌석 상태 FREE로 수정
            reservationSeat.updateStatusReserveFREE();
            reservationSeat.delete(passport.getUserId());

            //TODO:: 좌석 반환 요청
            //TODO:: 결제 취소 요청
            //TODO:: 실패 알람 처리
            //Code를 가지고 내용 보내기
        });
    }

    @Override

    public boolean support(Event<SeatConfirmFailPayload> event) {
        return event.getEventType() == EventType.SEAT_CONFIRM_FAIL;
    }
}