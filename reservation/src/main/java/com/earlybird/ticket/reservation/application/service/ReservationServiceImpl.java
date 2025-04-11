package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.common.exception.CustomJsonProcessingException;
import com.earlybird.ticket.reservation.domain.dto.request.SeatAssignPayload;
import com.earlybird.ticket.reservation.domain.entity.*;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.OutboxRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final PassportUtil passportUtil;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void createResrvation(List<CreateReservationCommand> createReservationCommands,
                                 String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        Long userId = passportDto.getUserId();

        // ✅ 여러 개의 payload를 리스트로 생성
        List<SeatAssignPayload> payloadList = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();

        for (CreateReservationCommand command : createReservationCommands) {
            // 1. 예약 생성
            Reservation reservation = createReservation(command,
                                                        userId);
            reservation = reservationRepository.save(reservation);
            reservations.add(reservation);

            // 2. 좌석 선점 생성
            ReservationSeat reservationSeat = createReservationSeat(command,
                                                                    reservation);
            reservationSeatRepository.save(reservationSeat);

            // 3. 아웃박스 payload 항목 누적
            SeatAssignPayload payload = SeatAssignPayload.builder()
                                                         .seatInstanceId(reservationSeat.getSeatInstanceId())
                                                         .userId(userId)
                                                         .role(Role.from(passportDto.getUserRole()))
                                                         .build();

            payloadList.add(payload);
        }
        SeatAssignPayloadListWrapper wrapper = new SeatAssignPayloadListWrapper(payloadList);

        Event<SeatAssignPayloadListWrapper> event = new Event<>(EventType.INSTANCE_SEAT_RESERVATION,
                                                                wrapper,
                                                                LocalDateTime.now()
                                                                             .toString());

        String payloadJson;
        try {
            payloadJson = new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new CustomJsonProcessingException();
        }

        Outbox outbox = Outbox.builder()
                              .aggregateType(Outbox.AggregateType.RESERVATION)
                              .aggregateId(reservations.get(0)
                                                       .getId()) // 또는 UUID.randomUUID()도 가능
                              .eventType(EventType.INSTANCE_SEAT_RESERVATION)
                              .payload(payloadJson)
                              .build();

        outboxRepository.save(outbox);
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


    private Reservation createReservation(CreateReservationCommand createReservationCommand,
                                          Long userId) {
        return Reservation.createReservation(userId,
                                             createReservationCommand.userName(),
                                             createReservationCommand.concertId(),
                                             createReservationCommand.concertName(),
                                             createReservationCommand.concertSequenceId(),
                                             createReservationCommand.concertSequenceStartDatetime(),
                                             createReservationCommand.concertSequenceEndDatetime(),
                                             createReservationCommand.concertSequenceStatus(),
                                             createReservationCommand.venueId(),
                                             createReservationCommand.venueArea(),
                                             createReservationCommand.venueLocation(),
                                             createReservationCommand.content(),
                                             createReservationCommand.couponId(),
                                             createReservationCommand.couponType(),
                                             createReservationCommand.couponName(),
                                             createReservationCommand.couponStatus(),
                                             createReservationCommand.hallId(),
                                             createReservationCommand.hallName(),
                                             createReservationCommand.hallFloor());
    }

    private ReservationSeat createReservationSeat(CreateReservationCommand createReservationCommand,
                                                  Reservation reservation) {
        return ReservationSeat.createReservationSeat(reservation,
                                                     createReservationCommand.seatInstanceId(),
                                                     createReservationCommand.concertId(),
                                                     createReservationCommand.seatRow(),
                                                     createReservationCommand.seatCol(),
                                                     createReservationCommand.seatGrade(),
                                                     createReservationCommand.seatPrice(),
                                                     // ← 이건 누락되어 있다면 Command에도 price 추가 필요
                                                     createReservationCommand.seatStatus());
    }
}
