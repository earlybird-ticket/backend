package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.common.exception.CustomJsonProcessingException;
import com.earlybird.ticket.reservation.domain.dto.request.PreemptSeatPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
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
import java.util.UUID;

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

        List<UUID> seatInstanceList = new ArrayList<>();
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

            // 3. 좌석 UUID 누적
            seatInstanceList.add(reservationSeat.getSeatInstanceId());
        }

        // 4. 아웃박스 payload 생성 (seatInstanceIds를 한 번에)
        PreemptSeatPayload payload = PreemptSeatPayload.createSeatPreemptPayload(seatInstanceList,
                                                                                 userId,
                                                                                 passportDto);

        Event<PreemptSeatPayload> event = new Event<>(EventType.SEAT_INSTANCE_RESERVATION,
                                                      payload,
                                                      LocalDateTime.now()
                                                                   .toString());

        String payloadJson;
        try {
            payloadJson = new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new CustomJsonProcessingException();
        }

        // 5. Outbox 저장
        Outbox outbox = Outbox.builder()
                              .aggregateType(Outbox.AggregateType.RESERVATION)
                              .aggregateId(reservations.get(0)
                                                       .getId())
                              .eventType(EventType.SEAT_INSTANCE_RESERVATION)
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
                                                     createReservationCommand.seatPrice());
    }
}
