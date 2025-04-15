package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.application.dto.response.SeatReturnFailPayload;
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

public class ReturnSeatFailPayloadHandler implements EventHandler<SeatReturnFailPayload> {

    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    @Transactional
    public void handle(Event<SeatReturnFailPayload> event) {
        SeatReturnFailPayload payload = event.getPayload();


        List<ReservationSeat> seatIntanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        PassportDto passport = payload.passportDto();
        seatIntanceList.forEach(reservationSeat -> {
            reservationSeat.getReservation()
                           .delete(passport.getUserId());

            //예약 좌석 상태 FREE로 수정
            reservationSeat.updateStatusReserveFREE();
            reservationSeat.delete(passport.getUserId());

            //TODO:: 실패 알람(메일) 처리
            //Code를 가지고 내용 보내기
        });
    }

    @Override

    public boolean support(Event<SeatReturnFailPayload> event) {
        return event.getEventType() == EventType.SEAT_RETURN_FAIL;
    }
}