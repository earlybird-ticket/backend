package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.common.exception.CustomJsonProcessingException;
import com.earlybird.ticket.reservation.common.exception.NotFoundReservationException;
import com.earlybird.ticket.reservation.common.exception.SeatAlreadyReservedException;
import com.earlybird.ticket.reservation.domain.dto.request.SeatReservePayload;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
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
        // 1. 좌석 예약 유무 확인
        validateReservationCommandsSeatInstanceId(createReservationCommands);

        for (CreateReservationCommand command : createReservationCommands) {
            // 2. 예약 생성
            Reservation reservation = createReservation(command,
                                                        userId);
            reservation = reservationRepository.save(reservation);
            reservations.add(reservation);

            // 3. 예약 좌석 생성(기본적으로 예약된상태)
            ReservationSeat reservationSeat = createReservationSeat(command,
                                                                    reservation);
            reservationSeatRepository.save(reservationSeat);

            // 4. 좌석 UUID 누적
            seatInstanceList.add(reservationSeat.getSeatInstanceId());
        }

        // 4. 아웃박스 payload 생성 (seatInstanceIds를 한 번에)
        SeatReservePayload payload = SeatReservePayload.createSeatPreemptPayload(seatInstanceList,
                                                                                 userId,
                                                                                 passportDto);

        Event<SeatReservePayload> event = new Event<>(EventType.INSTANCE_SEAT_RESERVATION,
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
                              .eventType(EventType.INSTANCE_SEAT_RESERVATION)
                              .payload(payloadJson)
                              .build();

        outboxRepository.save(outbox);
    }

    private void validateReservationCommandsSeatInstanceId(List<CreateReservationCommand> createReservationCommands) {
        createReservationCommands.forEach(command -> {
            //해당 좌석이 FREE가 아닌 상태가 있다면
            boolean isSeatReservationExist = reservationSeatRepository.existsBySeatInstanceIdAndSeatStatusNotFREE(command.seatInstanceId(),
                                                                                                                  SeatStatus.FREE);

            //예외 발생
            if (isSeatReservationExist) {
                throw new SeatAlreadyReservedException();
            }

        });
    }

    @Override
    @Transactional
    public void cancelReservation(UUID reservationId,
                                  String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        //1. 예약 엔티티 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                                                       .orElseThrow(() -> new NotFoundReservationException());
        //2. 예약 취소
        reservation.cancelReservation(passportDto.getUserId());

        //3. 결제 취소 이벤트 발행

    }

    @Override
    @Transactional(readOnly = true)
    public FindReservationQuery findReservation(UUID reservationId,
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
