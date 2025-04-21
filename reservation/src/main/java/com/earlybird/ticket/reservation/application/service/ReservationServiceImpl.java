package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.common.exception.NotFoundReservationException;
import com.earlybird.ticket.reservation.common.exception.SeatAlreadyReservedException;
import com.earlybird.ticket.reservation.common.util.EventPayloadConverter;
import com.earlybird.ticket.reservation.domain.dto.request.PreemptSeatDltEvent;
import com.earlybird.ticket.reservation.domain.dto.request.ReturnCouponEvent;
import com.earlybird.ticket.reservation.domain.dto.request.ReturnSeatEvent;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final PassportUtil passportUtil;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final OutboxRepository outboxRepository;
    private final EventPayloadConverter eventPayloadConverter;
    private final RedissonClient redissonClient;

    //
    //    //TODO::validateReservationCommandsSeatInstanceId(createReservationCommands)을 삭제하고 Redis캐싱을 통해 이미 선점된 좌석인지 체크하고 락 획득
    //    @Override
    //    @Transactional
    //    public String createReservation(CreateReservationCommand createReservationCommands,
    //                                    String passport) {
    //
    //        PassportDto passportDto = passportUtil.getPassportDto(passport);
    //        Long userId = passportDto.getUserId();
    //
    //        List<UUID> instanceSeatList = createReservationCommands.seatList()
    //                                                               .stream()
    //                                                               .map(SeatRequest::seatInstanceId)
    //                                                               .toList();
    //
    //        // MultiRock 생성
    //        List<RLock> seatLocks = instanceSeatList.stream()
    //                                                .map(id -> redissonClient.getLock("lock:seatInstance:" + id))
    //                                                .toList();
    //
    //        RedissonMultiLock multiLock = new RedissonMultiLock(seatLocks.toArray(new RLock[0]));
    //
    //        int maxAttempts = 3;
    //        boolean locked = false;
    //        Random random = new Random();
    //        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
    //            try {
    //                locked = multiLock.tryLock(3,
    //                                           10,
    //                                           TimeUnit.SECONDS);
    //                if (locked) {
    //                    break;
    //                }
    //
    //                log.warn("[Retry:{}] 좌석 락 획득 실패. 재시도 예정",
    //                         attempt);
    //                long sleepMillis = (long) (Math.pow(2,
    //                                                    attempt) * 100L + random.nextInt(150));
    //                Thread.sleep(sleepMillis);
    //            } catch (InterruptedException e) {
    //                Thread.currentThread()
    //                      .interrupt();
    //                throw new InternalServerErrorException("락 재시도 중 인터럽트 발생");
    //            }
    //        }
    //        if (!locked) {
    //            log.error("좌석 락 시도 3회 초과");
    //            throw new LockFailedException();
    //        }
    //        validateReservationCommandsSeatInstanceId(createReservationCommands);
    //
    //        try {
    //            // 2. 예약 생성
    //            Reservation reservation = createReservation(createReservationCommands,
    //                                                        userId);
    //            reservation = reservationRepository.save(reservation);
    //
    //            // 3. 예약 좌석 생성(기본적으로 예약된상태)
    //            List<ReservationSeat> reservationSeatList = createReservationSeat(createReservationCommands,
    //                                                                              reservation);
    //            if (createReservationCommands.seatList() == null || createReservationCommands.seatList()
    //                                                                                         .isEmpty()) {
    //                throw new IllegalArgumentException("seatList is empty or null");
    //            }
    //
    //            reservationSeatRepository.saveAll(reservationSeatList);
    //
    //            // 4. 아웃박스 payload 생성 (seatInstanceIds를 한 번에)
    //            if (instanceSeatList.isEmpty()) {
    //                throw new NullPointerException();
    //            }
    //
    //            PreemptSeatEvent payload = PreemptSeatEvent.createSeatPreemptPayload(instanceSeatList,
    //                                                                                 passportDto,
    //                                                                                 reservation.getId());
    //
    //            Event<PreemptSeatEvent> event = new Event<>(EventType.SEAT_PREEMPT,
    //                                                        payload,
    //                                                        LocalDateTime.now()
    //                                                                     .toString());
    //
    //            if (event == null) {
    //                throw new IllegalStateException("event is null before serialization");
    //            }
    //
    //            String payloadJson;
    //            payloadJson = new ObjectMapper().writeValueAsString(event);
    //
    //            // 5. Outbox 저장
    //            // 동기 처리시에는 Outbox패턴이 아닌 커밋이전에 데이터를 발행하고 응답을 받아오는 방식으로 수행해야 함
    //            Outbox outbox = Outbox.builder()
    //                                  .aggregateType(Outbox.AggregateType.RESERVATION)
    //                                  .aggregateId(reservation.getId())
    //                                  .eventType(EventType.SEAT_PREEMPT)
    //                                  .payload(payloadJson)
    //                                  .build();
    //
    //            outboxRepository.save(outbox);
    //
    //            //6. 예약 ID값 반환
    //            return reservation.getId()
    //                              .toString();
    //        } catch (JsonProcessingException e) {
    //            throw new CustomJsonProcessingException();
    //        } catch (Exception e) {
    //            sendToDLT(instanceSeatList,
    //                      passportDto);
    //        } finally {
    //            if (locked) {
    //                multiLock.unlock();
    //            }
    //        }
    //        return null;
    //    }

    private void sendToDLT(List<UUID> instanceSeatList,
                           PassportDto passportDto) {
        Event<PreemptSeatDltEvent> preemptSeatDltEventEvent = new Event<>(EventType.RESERVATION_LOCK_FAIL,
                                                                          PreemptSeatDltEvent.createSeatPreemptPayload(instanceSeatList,
                                                                                                                       passportDto),
                                                                          LocalDateTime.now()
                                                                                       .toString());
        String payload = eventPayloadConverter.serializePayload(preemptSeatDltEventEvent);

        Outbox dltOutbox = Outbox.builder()
                                 .aggregateId(instanceSeatList.get(0))
                                 .aggregateType("DLT")
                                 .eventType(EventType.RESERVATION_LOCK_FAIL)
                                 .payload(payload)
                                 .build();

        outboxRepository.save(dltOutbox);

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

        Event<ReturnSeatEvent> returnSeatPayloadEvent = new Event<>(EventType.SEAT_RETURN,
                                                                    ReturnSeatEvent.builder()
                                                                                   .seatInstanceIdList(reservationSeatList.stream()
                                                                                                                          .map(ReservationSeat::getId)
                                                                                                                          .toList())
                                                                                   .passportDto(passportDto)
                                                                                   .build(),
                                                                    LocalDateTime.now()
                                                                                 .toString());
        String payloadRecord = eventPayloadConverter.serializePayload(returnSeatPayloadEvent);

        Outbox seatOutbox = Outbox.builder()
                                  .aggregateId(reservation.getId())
                                  .aggregateType("RESERVATION")
                                  .eventType(EventType.SEAT_RETURN)
                                  .payload(payloadRecord)
                                  .build();

        outboxRepository.save(seatOutbox);

        reservation.delete(passportDto.getUserId());
        reservationSeatList.forEach(seat -> seat.delete(passportDto.getUserId()));

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
}
