package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.common.exception.CustomJsonProcessingException;
import com.earlybird.ticket.reservation.common.exception.NotFoundReservationException;
import com.earlybird.ticket.reservation.common.exception.SeatAlreadyReservedException;
import com.earlybird.ticket.reservation.common.util.EventPayloadConverter;
import com.earlybird.ticket.reservation.domain.dto.request.PreemptSeatPayload;
import com.earlybird.ticket.reservation.domain.dto.request.ReturnCouponEvent;
import com.earlybird.ticket.reservation.domain.dto.request.ReturnSeatPayload;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final EventPayloadConverter eventPayloadConverter;

    @Override
    @Transactional
    public String createReservation(CreateReservationCommand createReservationCommands,
                                    String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        Long userId = passportDto.getUserId();

        List<UUID> seatInstanceList = new ArrayList<>();
        // 1. 좌석 예약 유무 확인
        validateReservationCommandsSeatInstanceId(createReservationCommands);

        // 2. 예약 생성
        Reservation reservation = createReservation(createReservationCommands,
                                                    userId);
        reservation = reservationRepository.save(reservation);

        // 3. 예약 좌석 생성(기본적으로 예약된상태)
        List<ReservationSeat> reservationSeatList = createReservationSeat(createReservationCommands,
                                                                          reservation);
        List<ReservationSeat> reservationSeats = reservationSeatRepository.saveAll(reservationSeatList);
        reservationSeats.forEach(seat -> seatInstanceList.add(seat.getSeatInstanceId()));


        // 4. 아웃박스 payload 생성 (seatInstanceIds를 한 번에)
        if (seatInstanceList.isEmpty()) {
            throw new NullPointerException();
        }
        PreemptSeatPayload payload = PreemptSeatPayload.createSeatPreemptPayload(seatInstanceList,
                                                                                 userId,
                                                                                 passportDto);

        Event<PreemptSeatPayload> event = new Event<>(EventType.SEAT_INSTANCE_PREEMPTION,
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
                              .aggregateId(reservation.getId())
                              .eventType(EventType.SEAT_INSTANCE_PREEMPTION)
                              .payload(payloadJson)
                              .build();

        outboxRepository.save(outbox);

        //6. 예약 ID값 반환
        return reservation.getId()
                          .toString();
    }

    @Override
    @Transactional
    public void cancelReservation(UUID reservationId,
                                  String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        //1. 예약 엔티티 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                                                       .orElseThrow(NotFoundReservationException::new);

        //2. 반환 좌석 조회
        List<ReservationSeat> reservationSeatList = reservationSeatRepository.findByReservation(reservation);
        //2. 예약 취소
        UUID paymentId = reservation.getPaymentId();

        // 2-2. 결제 취소 요청
        // TODO:: 결제 취소 성공시 좌석 반환 요청 및 쿠폰 복구 요청
        //고도화에서 진행할 예정


        Event<ReturnCouponEvent> returnCouponEvent = new Event<>(EventType.COUPON_RETURN,
                                                                 ReturnCouponEvent.builder()
                                                                                  .passportDto(passportDto)
                                                                                  .code("C01")
                                                                                  .couponId(reservation.getCouponId())
                                                                                  .build(),
                                                                 LocalDateTime.now()
                                                                              .toString());

        String couponRecord = eventPayloadConverter.serializePayload(returnCouponEvent);

        Outbox couponOutbox = Outbox.builder()
                                    .aggregateId(reservation.getId())
                                    .aggregateType("RESERVATION")
                                    .eventType(EventType.COUPON_RETURN)
                                    .payload(couponRecord)
                                    .build();

        outboxRepository.save(couponOutbox);


        Event<ReturnSeatPayload> returnSeatPayloadEvent = new Event<>(EventType.SEAT_INSTANCE_RETURN,
                                                                      ReturnSeatPayload.builder()
                                                                                       .seatInstanceList(reservationSeatList.stream()
                                                                                                                            .map(ReservationSeat::getId)
                                                                                                                            .toList())
                                                                                       .passportDto(passportDto)
                                                                                       .build(),
                                                                      LocalDateTime.now()
                                                                                   .toString());
        //TODO :: serialize한 변수들 이름 일치시키기
        String payloadRecord = eventPayloadConverter.serializePayload(returnSeatPayloadEvent);

        Outbox seatOutbox = Outbox.builder()
                                  .aggregateId(reservation.getId())
                                  .aggregateType("RESERVATION")
                                  .eventType(EventType.SEAT_INSTANCE_RETURN)
                                  .payload(payloadRecord)
                                  .build();

        outboxRepository.save(seatOutbox);

        reservation.delete(passportDto.getUserId());
        reservationSeatList.forEach(seat -> {
            seat.delete(passportDto.getUserId());
        });

    }

    @Override
    @Transactional(readOnly = true)
    public FindReservationQuery findReservation(UUID reservationId,
                                                String passport) {

        Reservation reservation = reservationRepository.findById(reservationId)
                                                       .orElseThrow(NotFoundReservationException::new);

        List<ReservationSeat> reservationSeatList = reservationSeatRepository.findByReservation(reservation);

        return FindReservationQuery.createFindReservationQuery(reservation,
                                                               reservationSeatList);
    }

    @Override
    public Page<ReservationSearchResult> searchReservations(Pageable pageable,
                                                            String q,
                                                            String startTime,
                                                            String endTime,
                                                            String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        return reservationSeatRepository.searchReservations(q,
                                                            startTime,
                                                            endTime,
                                                            pageable,
                                                            passportDto);

    }


    private void validateReservationCommandsSeatInstanceId(CreateReservationCommand createReservationCommands) {
        //해당 좌석이 FREE가 아닌 상태가 있다면
        createReservationCommands.seatList()
                                 .forEach(seatRequest -> {
                                     boolean isSeatReservationExist = reservationSeatRepository.existsBySeatInstanceIdAndSeatStatusNotFREE(seatRequest.seatInstanceId(),
                                                                                                                                           SeatStatus.FREE);

                                     //예외 발생
                                     if (isSeatReservationExist) {
                                         throw new SeatAlreadyReservedException();
                                     }
                                 });

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

    private List<ReservationSeat> createReservationSeat(CreateReservationCommand createReservationCommand,
                                                        Reservation reservation) {
        return createReservationCommand.seatList()
                                       .stream()
                                       .map(seatCommand -> ReservationSeat.createReservationSeat(reservation,
                                                                                                 seatCommand.seatInstanceId(),
                                                                                                 createReservationCommand.concertId(),
                                                                                                 seatCommand.seatRow(),
                                                                                                 seatCommand.seatCol(),
                                                                                                 seatCommand.seatGrade(),
                                                                                                 seatCommand.seatPrice()))
                                       .toList();
    }
}
