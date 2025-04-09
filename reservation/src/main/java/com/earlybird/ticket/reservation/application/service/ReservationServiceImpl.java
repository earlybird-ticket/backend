package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.presentation.dto.response.FindReservationQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Override
    @Transactional
    public void createResrvation(CreateReservationCommand createReservationCommand) {
        //TODO::락 필요
        //1. 예약 엔티티 생성
        //2. 아웃박스 좌석 선점 이벤트 저장
        //3. 아웃박스 발행

    }

    @Override
    @Transactional
    public void cancelReservation(String reservationId,
                                  String passport) {
        //1. 예약 엔티티 조회
        //2. 예약 취소
        //3. 결제 취소 이벤트 발행
    }

    @Override
    @Transactional(readOnly = true)
    public FindReservationQuery findReservation(String reservationId,
                                                String passport) {
        return null;
    }


}
